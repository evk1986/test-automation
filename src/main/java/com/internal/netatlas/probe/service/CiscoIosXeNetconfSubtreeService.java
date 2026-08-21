package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.model.DeviceSnapshot;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.policy.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class CiscoIosXeNetconfSubtreeService {

    private final NetconfAdapter netconfAdapter;
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final RetryTemplate retryTemplate;
    private final CircuitBreaker circuitBreaker;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public CiscoIosXeNetconfSubtreeService(NetconfAdapter netconfAdapter,
                                            QueueMessagingTemplate queueMessagingTemplate) {
        this.netconfAdapter = netconfAdapter;
        this.queueMessagingTemplate = queueMessagingTemplate;

        // Exponential retry: max 3 attempts, initial interval 500ms, multiplier 2.0
        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(500L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(4000L);
        RetryPolicy retryPolicy = new SimpleRetryPolicy(3);
        this.retryTemplate = new RetryTemplate();
        this.retryTemplate.setBackOffPolicy(backOff);
        this.retryTemplate.setRetryPolicy(retryPolicy);

        // Circuit breaker: open after 5 failures, stay open 30 seconds
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(5)
                .slidingWindowSize(10)
                .build();
        this.circuitBreaker = CircuitBreaker.of("ciscoIosXeNetconf", cbConfig);
    }

    /**
     * Collects a subtree of IOS‑XE configuration via NETCONF, applies retry and circuit‑breaker protection,
     * stores the raw payload in a {@link DeviceSnapshot} and publishes a minimal message to the
     * {@code probe.commands} SQS queue for downstream processing.
     */
    public DeviceSnapshot collectSubtree(ProbeJob job) {
        Supplier<DeviceSnapshot> protectedSupplier = CircuitBreaker.decorateSupplier(
                circuitBreaker,
                () -> retryTemplate.execute(context -> {
                    // IOS‑XE subtree filter that retrieves interface configuration
                    String filter = "<filter type=\"subtree\"><native xmlns=\"http://cisco.com/ns/yang/Cisco-IOS-XE-native\"><interface/></native></filter>";
                    String raw = netconfAdapter.executeGet(job.getDeviceId(), filter);
                    DeviceSnapshot snapshot = new DeviceSnapshot();
                    snapshot.setDeviceId(job.getDeviceId());
                    snapshot.setProtocol(job.getProtocol());
                    snapshot.setRawPayload(raw);
                    snapshot.setCollectedAt(System.currentTimeMillis());
                    snapshot.setJobId(job.getId());
                    return snapshot;
                })
        );

        DeviceSnapshot snapshot = protectedSupplier.get();

        // Publish a lightweight JSON message to the probe.commands queue
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("jobId", job.getId());
        msg.put("deviceId", job.getDeviceId());
        msg.put("protocol", job.getProtocol());
        msg.put("rawPayload", snapshot.getRawPayload());
        queueMessagingTemplate.convertAndSend("probe.commands", msg.toString());

        return snapshot;
    }
}
