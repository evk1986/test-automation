package com.internal.netatlas.enrich.controller;

import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.service.DataEnricherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/enrich")
public class DataEnricherController {
    @Autowired
    private DataEnricherService enricherService;

    @GetMapping("/{recordId}/enrich")
    public EnrichmentResult enrich(@PathVariable String recordId) {
        // Implement enrichment API endpoint
        return enricherService.enrich(new NormalizedRecord());
    }
}