package com.internal.netatlas.probe.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.function.Supplier;

@Service
public class NetconfSessionResilienceService {

    private final Timer netconfLatencyTimer;
    private final int maxAttempts = 3;
    private final Duration backoff = Duration.ofSeconds(2);

    public NetconfSessionResilienceService(MeterRegistry meterRegistry) {
        this.netconfLatencyTimer = Timer.builder("netconf.ncs.latency")
                .description("Latency of NETCONF NCS session execution")
                .publishPercentileHistogram()
                .register(meterRegistry);
    }

    public <T> T executeWithResilience(Supplier<T> operation) {
        int attempts = 0;
        while (true) {
            attempts++;
            try {
                return netconfLatencyTimer.record(operation);
            } catch (Exception ex) {
                if (attempts >= maxAttempts) {
                    // Record failure metric by re‑recording the exception
                    netconfLatencyTimer.record(() -> { throw ex; });
                    throw new RuntimeException("NETCONF NCS operation failed after retries", ex);
                }
                try {
                    Thread.sleep(backoff.toMillis() * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
    }
}
