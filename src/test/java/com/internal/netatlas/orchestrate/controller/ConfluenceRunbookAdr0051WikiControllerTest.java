package com.internal.netatlas.orchestrate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConfluenceRunbookAdr0051Wiki.class)
public class ConfluenceRunbookAdr0051WikiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfluenceRunbookAdr0051WikiService service;

    @Test
    void getRunbook_returnsContent() throws Exception {
        String mockContent = "# Sample Runbook";
        when(service.getRunbookContent()).thenReturn(mockContent);

        mockMvc.perform(get("/api/v1/monitoring/runbook"))
                .andExpect(status().isOk())
                .andExpect(content().string(mockContent));
    }
}
