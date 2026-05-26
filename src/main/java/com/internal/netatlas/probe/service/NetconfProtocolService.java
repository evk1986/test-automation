package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetconfProtocolService {
    @Autowired
    private CiscoIosXrNetconfHandler ciscoIosXrNetconfHandler;

    public void processNetconfMessage(ProbeJobMessage message) {
        ciscoIosXrNetconfHandler.handle(message);
    }
}