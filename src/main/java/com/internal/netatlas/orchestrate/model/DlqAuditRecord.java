package com.internal.netatlas.orchestrate.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Audit record for each DLQ message processed by the drain workflow.
 */
@Table("probe.dlq_audit")
public class DlqAuditRecord {
    @Id
    private UUID id;
    private String messageId;
    private Instant processedAt;
    private String status; // SUCCESS or FAILURE

    public DlqAuditRecord() {
        this.id = UUID.randomUUID();
        this.processedAt = Instant.now();
    }

    public DlqAuditRecord(String messageId, String status) {
        this();
        this.messageId = messageId;
        this.status = status;
    }

    public UUID getId() { return id; }
    public String getMessageId() { return messageId; }
    public Instant getProcessedAt() { return processedAt; }
    public String getStatus() { return status; }

    public void setId(UUID id) { this.id = id; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }
    public void setStatus(String status) { this.status = status; }
}
