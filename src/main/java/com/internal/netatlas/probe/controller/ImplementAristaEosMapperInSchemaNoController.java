package com.internal.netatlas.probe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/probe/jobs")
@Tag(name = "ImplementAristaEosMapperInSchemaNo")
public class ImplementAristaEosMapperInSchemaNoController {
    private final ImplementAristaEosMapperInSchemaNoService service;

    public ImplementAristaEosMapperInSchemaNoController(ImplementAristaEosMapperInSchemaNoService service) {
        this.service = service;
    }

    @GetMapping("/implement-arista-eos-mapper-in-schema-no")
    @Operation(summary = "Implement Arista EOS mapper in Schema-Normalizer")
    public ResponseEntity<String> handle() {
        return ResponseEntity.ok(service.execute());
    }
}
