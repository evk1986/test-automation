package com.internal.netatlas.orchestrate.repository;

import com.internal.netatlas.orchestrate.model.DlqAuditRecord;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Cassandra repository for DLQ audit records.
 */
@Repository
public interface DlqAuditRepository extends CassandraRepository<DlqAuditRecord, java.util.UUID> {
    // No custom methods needed for basic CRUD
}
