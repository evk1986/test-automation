package com.internal.netatlas.normalize.repository;

import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalizedRecordRepository extends CassandraRepository<NormalizedRecord, String> {
}
