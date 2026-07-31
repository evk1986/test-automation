package com.internal.netatlas.enrich.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformWikiAdr0045Repository extends CassandraRepository<IdempotencyKey, String> {
    // No additional methods needed for count()
}
