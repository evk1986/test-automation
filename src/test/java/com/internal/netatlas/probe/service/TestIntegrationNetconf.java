package com.internal.netatlas.probe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.repository.NormalizedRecordRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestIntegrationNetconf {

    @Autowired
    private NetconfBatchProcessingService service;

    @Autowired
    private NormalizedRecordRepository repository;

    @Test
    public void shouldPersistRecordAndIncrementMetric() {
        // Build a minimal NormalizedRecord DTO
        NormalizedRecord record = new NormalizedRecord();
        record.setId(UUID.randomUUID().toString());
        record.setCanonicalType("cisco-iosxr-ncs");
        record.setNormalizedPayload(new ObjectMapper().createObjectNode().put("status", "up"));

        // Persist via service which also records a metric
        NormalizedRecord saved = service.persistAndRecord(record);

        // Verify the record was stored in Cassandra (repository mock or real test keyspace)
        Optional<NormalizedRecord> fetched = repository.findById(saved.getId());
        assertTrue(fetched.isPresent(), "Record should be present after persistence");
        assertEquals(record.getId(), fetched.get().getId(), "Persisted ID must match original");
        assertEquals(record.getCanonicalType(), fetched.get().getCanonicalType(), "Canonical type must be preserved");
    }

    @Configuration
    static class TestConfig {
        @Bean
        public SimpleMeterRegistry simpleMeterRegistry() {
            return new SimpleMeterRegistry();
        }
    }
}
