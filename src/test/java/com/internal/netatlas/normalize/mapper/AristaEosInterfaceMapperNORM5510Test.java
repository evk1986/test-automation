package com.internal.netatlas.normalize.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AristaEosInterfaceMapperNORM5510} covering a representative set of EOS interface payloads.
 * Ticket: NORM-5510
 */
class AristaEosInterfaceMapperNORM5510Test {

    private AristaEosInterfaceMapperNORM5510 mapper;

    @BeforeEach
    void setUp() {
        mapper = new AristaEosInterfaceMapperNORM5510();
    }

    @Test
    @DisplayName("Map 12 sample EOS interface payloads – expect successful NormalizedRecord creation")
    void mapSamplePayloads() throws IOException {
        String[] samples = new String[]{
                "{\"name\":\"Ethernet1\",\"state\":\"up\",\"speed\":\"100G\"}",
                "{\"name\":\"Ethernet2\",\"state\":\"down\",\"speed\":\"10G\"}",
                "{\"name\":\"Ethernet3\",\"state\":\"up\",\"speed\":\"40G\"}",
                "{\"name\":\"Ethernet4\",\"state\":\"up\",\"speed\":\"1G\"}",
                "{\"name\":\"Ethernet5\",\"state\":\"down\",\"speed\":\"10G\"}",
                "{\"name\":\"Ethernet6\",\"state\":\"up\",\"speed\":\"100G\"}",
                "{\"name\":\"Ethernet7\",\"state\":\"up\",\"speed\":\"40G\"}",
                "{\"name\":\"Ethernet8\",\"state\":\"down\",\"speed\":\"1G\"}",
                "{\"name\":\"Ethernet9\",\"state\":\"up\",\"speed\":\"10G\"}",
                "{\"name\":\"Ethernet10\",\"state\":\"down\",\"speed\":\"100G\"}",
                "{\"name\":\"Ethernet11\",\"state\":\"up\",\"speed\":\"40G\"}",
                "{\"name\":\"Ethernet12\",\"state\":\"up\",\"speed\":\"1G\"}"
        };
        for (String json : samples) {
            NormalizedRecord record = mapper.map(json);
            assertNotNull(record, "Mapper should return a NormalizedRecord");
            assertEquals("AristaInterface", record.getCanonicalType(), "Canonical type must be AristaInterface");
            JsonNode payload = record.getNormalizedPayload();
            assertNotNull(payload, "Normalized payload must not be null");
            assertTrue(payload.has("name"), "Payload must contain interface name");
        }
    }
}
