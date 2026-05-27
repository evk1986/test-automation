package com.internal.netatlas.normalize.repository;

import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AristaEosGoldenFileTestRepository extends CrudRepository<NormalizedRecord, String> {
    List<NormalizedRecord> findAllByDeviceFamily(String deviceFamily);
}