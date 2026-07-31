package com.internal.netatlas.probe.protocol;

import com.internal.netatlas.probe.model.ProbeJobMessage;

/**
 * Minimal adapter contract for executing NETCONF subtree operations.
 * Implementations are protocol‑specific (e.g., Cisco IOS‑XR NCS).
 */
public interface NetconfAdapter {
    /**
     * Executes a NETCONF subtree query for the supplied job and returns the raw XML payload.
     *
     * @param job the probe job containing device and authentication details
     * @return raw NETCONF response as a String
     * @throws Exception if the remote call fails or the device is unreachable
     */
    String executeSubtree(ProbeJobMessage job) throws Exception;
}
