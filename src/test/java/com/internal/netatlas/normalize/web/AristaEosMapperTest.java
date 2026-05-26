package com.internal.netatlas.normalize.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.internal.netatlas.normalize.mapper.AristaEosMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AristaEosMapperTest {

    @Autowired
    private AristaEosMapper aristaEosMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAristaEosMapper() throws Exception {
        JsonNode eosResponse = JsonNodeFactory.instance.objectNode();
        NormalizedRecord record = aristaEosMapper.map(eosResponse);
        mockMvc.perform(get("/normalize/arista-eos")).andExpect(status().isOk());
    }
}