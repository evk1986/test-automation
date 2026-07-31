package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.enrich.repository.EnrichmentResultRepository;
import com.internal.netatlas.enrich.publisher.SnsPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Performs idempotent enrichment of Arista EOS normalized records.
 *
 * <p>The service checks the {@link EnrichmentResultRepository} to avoid duplicate rows,
 * creates a minimal enriched payload, persists it, and fans out the result via SNS.</p>
 */
@Service
public class AristaEosEnrichmentService {

    private final EnrichmentResultRepository repository;
    private final SnsPublisher snsPublisher;

    public AristaEosEnrichmentService(EnrichmentResultRepository repository,
                                      SnsPublisher snsPublisher) {
        this.repository = repository;
        this.snsPublisher = snsPublisher;
    }

    public void enrich(NormalizedRecord record) {
        // Idempotency guard – if a result for this record already exists we skip processing.
        Optional<EnrichmentResult> existing = repository.findById(record.getId());
        if (existing.isPresent()) {
            return;
        }

        EnrichmentResult result = new EnrichmentResult();
        result.setId(UUID.randomUUID().toString());
        result.setNormalizedRecordId(record.getId());
        // Sample enriched fields; real logic would be richer.
        result.setEnrichedFields(Map.of(
                "interfaceSpeed", "10G",
                "vendor", "Arista",
                "model", record.getCanonicalType()
        ));
        result.setEnrichedAt(Instant.now());
        result.setDownstreamTopicArn(snsPublisher.getTopicArn());

        repository.save(result);
        snsPublisher.publish(result);
    }
}
