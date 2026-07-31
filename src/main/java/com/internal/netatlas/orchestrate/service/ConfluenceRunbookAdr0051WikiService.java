package com.internal.netatlas.orchestrate.service;

import org.springframework.stereotype.Service;
import com.internal.netatlas.orchestrate.repository.ConfluenceRunbookAdr0051WikiRepository;

@Service
public class ConfluenceRunbookAdr0051WikiService {

    private final ConfluenceRunbookAdr0051WikiRepository repository;

    public ConfluenceRunbookAdr0051WikiService(ConfluenceRunbookAdr0051WikiRepository repository) {
        this.repository = repository;
    }

    public String getRunbookContent() {
        // In a real system the runbook would be stored in a wiki table.
        // Here we return a static markdown snippet for demonstration.
        return repository.findRunbook()
                .orElse("# Runbook not found\nPlease contact Ops lead.");
    }
}
