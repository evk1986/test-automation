package com.internal.netatlas.normalize.mapper;

import com.internal.netatlas.normalize.model.InterfaceRecord;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Norm5510ImplementSchemaNormalizerAristaEosMapperTest {

    private final Norm5510ImplementSchemaNormalizerAristaEosMapper mapper = new Norm5510ImplementSchemaNormalizerAristaEosMapper();

    @Test
    void mapValidJsonReturnsInterfaceRecord() {
        String json = "{ \"interfaces\": [ { \"name\": \"Ethernet1\", \"description\": \"Uplink\", \"adminStatus\": \"up\", \"operStatus\": \"up\", \"macAddress\": \"aa:bb:cc:dd:ee:ff\", \"speed\": 10000 } ] }";
        InterfaceRecord record = mapper.map(json);
        assertEquals("Ethernet1", record.getName());
        assertEquals("Uplink", record.getDescription());
        assertEquals("up", record.getAdminStatus());
        assertEquals("up", record.getOperStatus());
        assertEquals("aa:bb:cc:dd:ee:ff", record.getMacAddress());
        assertEquals(10000, record.getSpeed());
    }

    @Test
    void mapInvalidJsonThrowsException() {
        String badJson = "not a json";
        assertThrows(IllegalArgumentException.class, () -> mapper.map(badJson));
    }
}
