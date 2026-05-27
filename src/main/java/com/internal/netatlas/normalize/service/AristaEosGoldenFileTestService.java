package com.internal.netatlas.normalize.service;

import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.repository.AristaEosGoldenFileTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AristaEosGoldenFileTestService {
    private final AristaEosGoldenFileTestRepository aristaEosGoldenFileTestRepository;

    @Autowired
    public AristaEosGoldenFileTestService(AristaEosGoldenFileTestRepository aristaEosGoldenFileTestRepository) {
        this.aristaEosGoldenFileTestRepository = aristaEosGoldenFileTestRepository;
    }

    public List<NormalizedRecord> validateGoldenFileTests() {
        return aristaEosGoldenFileTestRepository.findAllByDeviceFamily("Arista EOS");
    }
}