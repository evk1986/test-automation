package com/internal/netatlas/probe/service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class NetconfCiscoIosXrNcsService {
    private static final Logger LOGGER = Logger.getLogger(NetconfCiscoIosXrNcsService.class.getName());

    @Autowired
    private NetconfCiscoIosXrNcsHandler handler;

    public void processNetconfResponse(ProbeJobMessage message) {
        // Implement business logic to process the NETCONF response
        LOGGER.info("Processing NETCONF response for device " + message.getDeviceId());
        // For simplicity, assume the response is logged and no further action is taken
    }
}