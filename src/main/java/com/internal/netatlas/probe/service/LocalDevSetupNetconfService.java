package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.stereotype.Service;

@Service
public class LocalDevSetupNetconfService {

    public InterfaceRecord process(ProbeJobMessage message) {
        String xml = message.getRawPayload();
        String interfaceName = extractInterfaceName(xml);
        return new InterfaceRecord(interfaceName, message.getDeviceId());
    }

    private String extractInterfaceName(String xml) {
        if (xml == null) {
            return "unknown";
        }
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("<name>([^<]+)</name>")
                .matcher(xml);
        return matcher.find() ? matcher.group(1) : "unknown";
    }

    public static class InterfaceRecord {
        private final String name;
        private final String deviceId;

        public InterfaceRecord(String name, String deviceId) {
            this.name = name;
            this.deviceId = deviceId;
        }

        public String getName() {
            return name;
        }

        public String getDeviceId() {
            return deviceId;
        }

        @Override
        public String toString() {
            return "InterfaceRecord{name='" + name + "', deviceId='" + deviceId + "'}";
        }
    }
}
