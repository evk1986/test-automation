package com.internal.netatlas.normalize.service;

import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.repository.NormalizedRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AristaEosValidationService {
    private final NormalizedRecordRepository normalizedRecordRepository;

    @Autowired
    public AristaEosValidationService(NormalizedRecordRepository normalizedRecordRepository) {
        this.normalizedRecordRepository = normalizedRecordRepository;
    }

    public boolean validateAristaEosData(NormalizedRecord record) {
        // Perform validation logic on the input record
        if (record.getDeviceFamily().equals("Arista EOS")) {
            return true;
        } else {
            return false;
        }
    }
}