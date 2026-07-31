package com.internal.netatlas.probe.controller;

import com.internal.netatlas.probe.service.ProbeLockService;
import com.internal.netatlas.probe.service.ProbeLockService.LockStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/probe/locks")
@Tag(name = "Probe Locks")
public class ProbeLockStatusController {

    private final ProbeLockService lockService;

    public ProbeLockStatusController(ProbeLockService lockService) {
        this.lockService = lockService;
    }

    @GetMapping("/{batchId}")
    @Operation(summary = "Get Hazelcast lock status for a probe batch")
    public ResponseEntity<LockStatusDto> getLockStatus(@PathVariable String batchId) {
        LockStatusDto status = lockService.getLockStatus(batchId);
        return ResponseEntity.ok(status);
    }
}
