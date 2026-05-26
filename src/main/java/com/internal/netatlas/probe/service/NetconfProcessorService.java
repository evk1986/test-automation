package com/internal/netatlas/probe/service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.stereotype.Service;

@Service
public class NetconfProcessorService {
    public void processNetconfMessage(ProbeJobMessage message) {
        // Process the NETCONF message
        System.out.println("Processing NETCONF message for device " + message.getDeviceId());
    }
}