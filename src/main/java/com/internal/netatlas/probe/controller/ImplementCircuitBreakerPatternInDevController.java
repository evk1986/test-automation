package com.internal.netatlas.probe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/probe/jobs")
@Tag(name = "ImplementCircuitBreakerPatternInDev")
public class ImplementCircuitBreakerPatternInDevController {
    private final ImplementCircuitBreakerPatternInDevService service;

    public ImplementCircuitBreakerPatternInDevController(ImplementCircuitBreakerPatternInDevService service) {
        this.service = service;
    }

    @GetMapping("/implement-circuit-breaker-pattern-in-dev")
    @Operation(summary = "Implement Circuit Breaker Pattern in Device-Probe for NETCONF and SSH Protocols")
    public ResponseEntity<String> handle() {
        return ResponseEntity.ok(service.execute());
    }
}
