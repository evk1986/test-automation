package com.internal.netatlas.probe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/probe/jobs")
@Tag(name = "ImplementAristaEosInterfacerecordMap")
public class ImplementAristaEosInterfacerecordMapController {
    private final ImplementAristaEosInterfacerecordMapService service;

    public ImplementAristaEosInterfacerecordMapController(ImplementAristaEosInterfacerecordMapService service) {
        this.service = service;
    }

    @GetMapping("/implement-arista-eos-interfacerecord-map")
    @Operation(summary = "Implement Arista EOS InterfaceRecord mapper in Schema-Normalizer")
    public ResponseEntity<String> handle() {
        return ResponseEntity.ok(service.execute());
    }
}
