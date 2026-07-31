package com.internal.netatlas.enrich.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.internal.netatlas.enrich.service.PlatformWikiAdr0045Service;

@RestController
@RequestMapping("/api/v1/enrich/idempotency")
@Tag(name = "Enrichment Idempotency")
public class PlatformWikiAdr0045Controller {

    private final PlatformWikiAdr0045Service service;

    public PlatformWikiAdr0045Controller(PlatformWikiAdr0045Service service) {
        this.service = service;
    }

    @GetMapping("/info")
    public ResponseEntity<String> getIdempotencyInfo() {
        String info = service.getIdempotencyInfo();
        return ResponseEntity.ok(info);
    }
}
