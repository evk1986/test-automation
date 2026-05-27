package com.internal/netatlas/normalize/repository;

import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.springframework.data.repository.CrudRepository;

public interface AristaEosGoldenFileTestRepository extends CrudRepository<NormalizedRecord, String> {
}