package com.internal.netatlas.probe.service;

import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Service;

/**
 * Service responsible for recording protocol‑specific failure metrics.
 * Call {@link #recordFailure(Protocol)} whenever a protocol interaction ends in error.
 */
@Service
public class ProtocolFailureMetricsService {

    public enum Protocol { NETCONF, SSH, SNMP, EAPI, GRPC }

    private final Counter netconfFailureCounter;
    private final Counter sshFailureCounter;
    private final Counter snmpFailureCounter;
    private final Counter eapiFailureCounter;
    private final Counter grpcFailureCounter;

    public ProtocolFailureMetricsService(Counter netconfFailureCounter,
                                         Counter sshFailureCounter,
                                         Counter snmpFailureCounter,
                                         Counter eapiFailureCounter,
                                         Counter grpcFailureCounter) {
        this.netconfFailureCounter = netconfFailureCounter;
        this.sshFailureCounter = sshFailureCounter;
        this.snmpFailureCounter = snmpFailureCounter;
        this.eapiFailureCounter = eapiFailureCounter;
        this.grpcFailureCounter = grpcFailureCounter;
    }

    /**
     * Increment the failure counter for the supplied protocol.
     */
    public void recordFailure(Protocol protocol) {
        switch (protocol) {
            case NETCONF:
                netconfFailureCounter.increment();
                break;
            case SSH:
                sshFailureCounter.increment();
                break;
            case SNMP:
                snmpFailureCounter.increment();
                break;
            case EAPI:
                eapiFailureCounter.increment();
                break;
            case GRPC:
                grpcFailureCounter.increment();
                break;
            default:
                // No‑op for unknown protocols – should never happen
                break;
        }
    }
}
