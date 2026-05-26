package com.internal.netatlas.probe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/probe/jobs")
@Tag(name = "ImplementExponentialBackoffRetryInD")
public class ImplementExponentialBackoffRetryInDController {
    private final ImplementExponentialBackoffRetryInDService service;

    public ImplementExponentialBackoffRetryInDController(ImplementExponentialBackoffRetryInDService service) {
        this.service = service;
    }

    @GetMapping("/implement-exponential-backoff-retry-in-d")
    @Operation(summary = "Implement exponential-backoff retry in Device-Probe NETCONF worker")
    public ResponseEntity<String> handle() {
        return ResponseEntity.ok(service.execute());
    }
}
