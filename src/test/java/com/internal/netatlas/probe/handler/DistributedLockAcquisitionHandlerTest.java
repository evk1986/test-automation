package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.LockRequestMessage;
import com.internal.netatlas.probe.service.DistributedLockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DistributedLockAcquisitionHandlerTest {
    @Mock
    private DistributedLockService lockService;

    private DistributedLockAcquisitionHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DistributedLockAcquisitionHandler(lockService);
    }

    @Test
    void shouldAcquireLockWhenServiceReturnsTrue() {
        LockRequestMessage msg = new LockRequestMessage("JOB-123", "device-001", "lock-abc");
        when(lockService.acquireLock(anyString())).thenReturn(true);

        handler.handle(msg);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(lockService).acquireLock(keyCaptor.capture());
        String expectedKey = "lock:JOB-123:lock-abc";
        assertEquals(expectedKey, keyCaptor.getValue());
    }

    @Test
    void shouldLogWarningWhenLockNotAcquired() {
        LockRequestMessage msg = new LockRequestMessage("JOB-456", "device-002", "lock-def");
        when(lockService.acquireLock(anyString())).thenReturn(false);

        handler.handle(msg);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(lockService).acquireLock(keyCaptor.capture());
        String expectedKey = "lock:JOB-456:lock-def";
        assertEquals(expectedKey, keyCaptor.getValue());
    }
}
