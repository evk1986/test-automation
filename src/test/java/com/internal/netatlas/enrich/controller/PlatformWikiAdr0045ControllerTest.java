package com.internal.netatlas.enrich.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.internal.netatlas.enrich.service.PlatformWikiAdr0045Service;

@WebMvcTest(PlatformWikiAdr0045Controller.class)
public class PlatformWikiAdr0045ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlatformWikiAdr0045Service service;

    @Test
    void getIdempotencyInfo_returnsOk() throws Exception {
        when(service.getIdempotencyInfo()).thenReturn("Idempotency keys stored: 0");
        mockMvc.perform(get("/api/v1/enrich/idempotency/info"))
                .andExpect(status().isOk());
    }
}
