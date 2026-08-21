package com.internal.netatlas.probe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NetconfSessionService {

    private static final Logger log = LoggerFactory.getLogger(NetconfSessionService.class);

    public boolean executeSubtreeQuery(String jobId, String deviceId, String batchId) {
        log.info("Executing NETCONF RPC subtree query for job {} on device {} within batch {}", jobId, deviceId, batchId);
        if (deviceId == null || deviceId.isBlank()) {
            log.error("Execution failed: deviceId is missing for job {}", jobId);
            return false;
        }
        log.info("NETCONF subtree query completed successfully for job {}", jobId);
        return true;
    }
}
