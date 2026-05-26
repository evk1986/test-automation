package com.internal.netatlas.orchestrate.controller;

import com.internal.netatlas.orchestrate.service.CronOverlapGuardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CronOverlapGuardControllerTest {

    @Mock
    private CronOverlapGuardService cronOverlapGuardService;

    @InjectMocks
    private CronOverlapGuardController cronOverlapGuardController;

    @Test
    void testGetCronOverlapGuardStatus() {
        when(cronOverlapGuardService.isCronOverlapAllowed(new DailySweepJob())).thenReturn(true);
        ResponseEntity<Boolean> response = cronOverlapGuardController.getCronOverlapGuardStatus();
        assertEquals(true, response.getBody());
    }
}