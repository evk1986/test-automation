package com/internal/netatlas/orchestrate/controller;

import com.internal.netatlas.orchestrate.service.CronOverlapGuardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CronOverlapGuardController {

    private final CronOverlapGuardService cronOverlapGuardService;

    @Autowired
    public CronOverlapGuardController(CronOverlapGuardService cronOverlapGuardService) {
        this.cronOverlapGuardService = cronOverlapGuardService;
    }

    @GetMapping("/api/v1/cron-overlap-guard-status")
    public ResponseEntity<Boolean> getCronOverlapGuardStatus() {
        return ResponseEntity.ok(cronOverlapGuardService.isCronOverlapAllowed(new DailySweepJob()));
    }
}