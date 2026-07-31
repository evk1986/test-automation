package com.internal.netatlas.probe.controller;

import com.internal.netatlas.probe.service.ProbeLockService;
import com.internal.netatlas.probe.service.ProbeLockService.LockStatusDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProbeLockStatusController.class)
class ProbeLockStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProbeLockService lockService;

    @Test
    @DisplayName("GET /api/v1/probe/locks/{batchId} returns lock status JSON")
    void getLockStatus_returnsStatus() throws Exception {
        String batchId = "BATCH-PRB-20240523-USE1-01";
        LockStatusDto dto = new LockStatusDto(true, "owner-uuid-123", 172800000L);
        Mockito.when(lockService.getLockStatus(batchId)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/probe/locks/{batchId}", batchId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked", is(true)))
                .andExpect(jsonPath("$.ownerUuid", is("owner-uuid-123")))
                .andExpect(jsonPath("$.lockAcquiredAt", is(172800000)));
    }
}
