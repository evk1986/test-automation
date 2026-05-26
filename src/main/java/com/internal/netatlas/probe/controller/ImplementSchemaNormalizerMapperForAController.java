package com.internal.netatlas.probe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/probe/jobs")
@Tag(name = "ImplementSchemaNormalizerMapperForA")
public class ImplementSchemaNormalizerMapperForAController {
    private final ImplementSchemaNormalizerMapperForAService service;

    public ImplementSchemaNormalizerMapperForAController(ImplementSchemaNormalizerMapperForAService service) {
        this.service = service;
    }

    @GetMapping("/implement-schema-normalizer-mapper-for-a")
    @Operation(summary = "Implement Schema-Normalizer mapper for Arista EOS eAPI response")
    public ResponseEntity<String> handle() {
        return ResponseEntity.ok(service.execute());
    }
}
