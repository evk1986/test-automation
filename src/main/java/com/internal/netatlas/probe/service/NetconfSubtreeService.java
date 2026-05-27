package com/internal/netatlas/probe/service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetconfSubtreeService {
    private final NetconfSubtreeHandler netconfSubtreeHandler;

    @Autowired
    public NetconfSubtreeService(NetconfSubtreeHandler netconfSubtreeHandler) {
        this.netconfSubtreeHandler = netconfSubtreeHandler;
    }

    public void processNetconfSubtree(ProbeJobMessage message) {
        // Call the handleNetconfSubtree method of the NetconfSubtreeHandler
        netconfSubtreeHandler.handleNetconfSubtree(message);
    }
}