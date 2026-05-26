package com.internal.netatlas.probe;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImplementExponentialBackoffRetryInDService {
    private static final Logger log = LoggerFactory.getLogger(ImplementExponentialBackoffRetryInDService.class);

    public String execute() {
        log.info("Implement exponential-backoff retry in Device-Probe NETCONF worker — processing");
        // Add retry strategy interface to Device-Probe worker pool config, implement exponential backoff for NETCONF session timeo
        return "TES-2: processing complete";
    }
}
