package com/internal/netatlas/probe/service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.handler.NetconfSessionRetryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetconfSessionRetryService {
    private final NetconfSessionRetryHandler netconfSessionRetryHandler;

    @Autowired
    public NetconfSessionRetryService(NetconfSessionRetryHandler netconfSessionRetryHandler) {
        this.netconfSessionRetryHandler = netconfSessionRetryHandler;
    }

    public void retryNetconfSession(ProbeJob probeJob) {
        netconfSessionRetryHandler.handle(probeJob);
    }
}