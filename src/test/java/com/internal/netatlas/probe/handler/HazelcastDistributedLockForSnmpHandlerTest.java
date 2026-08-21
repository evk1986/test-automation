package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.service.HazelcastDistributedLockForSnmpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastDistributedLockForSnmpHandlerTest {

    @Mock
    private HazelcastDistributedLockForSnmpService lockService;

    private HazelcastDistributedLockForSnmpHandler handler;

    @BeforeEach
    void setUp() {
        handler = new HazelcastDistributedLockForSnmpHandler(lockService);
    }

    @Test
    void testHandleValidCommandSuccess() {
        var command = new HazelcastDistributedLockForSnmpHandler.SnmpProbeCommand("DEV-ASR1001-01", "MSG-88401", "BATCH-PRB-20240523-USE1-01", "SNMP");
        when(lockService.processSnmpWalkWithLock("DEV-ASR1001-01", "MSG-88401")).thenReturn(true);

        handler.handle(command);

        verify(lockService).processSnmpWalkWithLock("DEV-ASR1001-01", "MSG-88401");
    }

    @Test
    void testHandleDuplicateMessageSkipped() {
        var command = new HazelcastDistributedLockForSnmpHandler.SnmpProbeCommand("DEV-ASR1001-01", "MSG-88401-DUP", "BATCH-PRB-20240523-USE1-01", "SNMP");
        when(lockService.processSnmpWalkWithLock("DEV-ASR1001-01", "MSG-88401-DUP")).thenReturn(false);

        handler.handle(command);

        verify(lockService).processSnmpWalkWithLock("DEV-ASR1001-01", "MSG-88401-DUP");
    }

    @Test
    void testHandleNullCommand() {
        handler.handle(null);

        verify(lockService, never()).processSnmpWalkWithLock(anyString(), anyString());
    }
}
