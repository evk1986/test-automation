package com.internal.netatlas.normalize;

import com.internal.netatlas.normalize.handler.AristaEosGoldenFileValidator;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import com.internal.netatlas.normalize.repository.NormalizedRecordRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AristaEosGoldenFileValidatorTest {
    @Autowired
    private AristaEosGoldenFileValidator validator;

    @Autowired
    private NormalizedRecordRepository repository;

    @Test
    public void testValidateGoldenFileTests() {
        // Create a test NormalizedRecord for Arista EOS
        NormalizedRecord record = new NormalizedRecord();
        record.setDeviceFamily("Arista EOS");
        repository.save(record);
        // Call the validateGoldenFileTests method
        validator.validateGoldenFileTests();
    }
}