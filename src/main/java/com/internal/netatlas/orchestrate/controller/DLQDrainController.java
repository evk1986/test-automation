package com.internal.netatlas.orchestrate.controller;

import com.internal.netatlas.orchestrate.service.DLQDrainService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/v1/orchestrate/dlq")
public class DLQDrainController {

    private final DLQDrainService drainService;

    @Autowired
    public DLQDrainController(DLQDrainService drainService) {
        this.drainService = drainService;
    }

    @PostMapping("/drain")
    public ResponseEntity<String> drain(@RequestParam(defaultValue = "100") int maxMessages) {
        int processed = drainService.drainDlq(maxMessages);
        return ResponseEntity.ok("Drained " + processed + " messages from DLQ");
    }
}
