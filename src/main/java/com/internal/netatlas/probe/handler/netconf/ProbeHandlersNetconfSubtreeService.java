package com.internal.netatlas.probe.handler.netconf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Service
public class ProbeHandlersNetconfSubtreeService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void process(ProbeJobMessage message) {
        // Minimal parsing of NETCONF subtree XML payload
        String rawXml = message.getRawPayload();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(rawXml)));
            Node hostnameNode = doc.getElementsByTagName("hostname").item(0);
            String hostname = hostnameNode != null ? hostnameNode.getTextContent() : "unknown";

            ObjectNode json = objectMapper.createObjectNode();
            json.put("deviceId", message.getDeviceId());
            json.put("hostname", hostname);
            json.put("batchId", message.getBatchId());

            // In the real pipeline this JSON would be published to SNS; we log for visibility.
            System.out.println("Parsed NETCONF subtree for device " + message.getDeviceId() + ": " + json);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse NETCONF subtree XML", ex);
        }
    }
}
