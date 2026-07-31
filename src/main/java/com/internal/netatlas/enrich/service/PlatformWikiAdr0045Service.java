package com.internal.netatlas.enrich.service;

import org.springframework.stereotype.Service;
import com.internal.netatlas.enrich.repository.PlatformWikiAdr0045Repository;

@Service
public class PlatformWikiAdr0045Service {

    private final PlatformWikiAdr0045Repository repository;

    public PlatformWikiAdr0045Service(PlatformWikiAdr0045Repository repository) {
        this.repository = repository;
    }

    public String getIdempotencyInfo() {
        // Simple example: count stored idempotency keys
        long count = repository.count();
        return "Idempotency keys stored: " + count;
    }
}
