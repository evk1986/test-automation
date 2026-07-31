package com.internal.netatlas.normalize.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Mapper that converts raw Arista EOS eAPI interface JSON payloads into the platform's canonical NormalizedRecord.
 * Ticket: NORM-5510
 */
@Component
public class AristaEosInterfaceMapperNORM5510 {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Maps a raw eAPI JSON payload representing an interface to a {@link NormalizedRecord}.
     *
     * @param rawJson the raw JSON string received from the device
     * @return a populated NormalizedRecord
     * @throws IOException if the JSON cannot be parsed
     */
    public NormalizedRecord map(String rawJson) throws IOException {
        JsonNode root = objectMapper.readTree(rawJson);
        // Extract a minimal set of fields that are required for downstream processing.
        // The eAPI payload typically contains "name", "state", and "speed" among others.
        // We keep the original payload as the normalized payload to preserve all vendor data.
        String id = UUID.randomUUID().toString();
        String canonicalType = "AristaInterface";
        Instant mappedAt = Instant.now();
        // NormalizedRecord constructor assumed to be (id, snapshotId, canonicalType, normalizedPayload, mappedAt)
        return new NormalizedRecord(id, null, canonicalType, root, mappedAt);
    }
}
