package com.internal.netatlas.orchestrate.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/monitoring")
@Tag(name = "Monitoring Runbook")
public class ConfluenceRunbookAdr0051Wiki {

    private final ConfluenceRunbookAdr0051WikiService service;

    public ConfluenceRunbookAdr0051Wiki(ConfluenceRunbookAdr0051WikiService service) {
        this.service = service;
    }

    @GetMapping("/runbook")
    @Operation(summary = "Retrieve monitoring runbook for failure‑rate metrics")
    public ResponseEntity<String> getRunbook() {
        String markdown = service.getRunbookContent();
        return ResponseEntity.ok(markdown);
    }
}
