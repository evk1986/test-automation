package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.ProbeJobStatus;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocsRunbookNetconfServiceTest {

    @Mock
    private ProbeJobRepository repository;

    @InjectMocks
    private DocsRunbookNetconfService service;

    @Test
    void process_successfulExecution_updatesJobStatus() {
        // Arrange
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setJobId("JOB-NETCONF-4821");
        msg.setDeviceId("device-001");
        msg.setProtocol("NETCONF");

        ProbeJob job = new ProbeJob();
        job.setId("JOB-NETCONF-4821");
        job.setDeviceId("device-001");
        job.setProtocol("NETCONF");
        job.setStatus(ProbeJobStatus.PENDING);
        job.setAttemptCount(0);

        when(repository.findById("JOB-NETCONF-4821")).thenReturn(Optional.of(job));
        when(repository.save(any(ProbeJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        service.process(msg);

        // Assert
        assertEquals(ProbeJobStatus.SUCCESS, job.getStatus(), "Job status should be SUCCESS after simulated execution");
        assertEquals(1, job.getAttemptCount(), "Attempt count should be incremented");
        verify(repository).save(job);
    }
}
