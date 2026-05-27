package com.internal.netatlas.normalize.service;

import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AristaEosGoldenFileTestService {
    public List<NormalizedRecord> getNormalizedRecords() {
        // Return list of normalized records for Arista EOS devices
        return List.of(new NormalizedRecord());
    }
}