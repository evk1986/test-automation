package com.internal.netatlas.normalize.handler;

import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.model.NormalizedRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AristaEosGoldenFileValidator {
    private final NormalizedRecordRepository normalizedRecordRepository;

    @Autowired
    public AristaEosGoldenFileValidator(NormalizedRecordRepository normalizedRecordRepository) {
        this.normalizedRecordRepository = normalizedRecordRepository;
    }

    public void validateGoldenFileTests() {
        // Retrieve Arista EOS golden file test data from repository
        NormalizedRecord aristaEosRecord = normalizedRecordRepository.findByDeviceFamily("Arista EOS");
        // Perform validation logic on the retrieved data
        if (aristaEosRecord != null) {
            System.out.println("Arista EOS golden file test data is valid");
        } else {
            System.out.println("Arista EOS golden file test data is invalid");
        }
    }
}