package com.internal.netatlas.probe.repository;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.data.repository.CrudRepository;

public interface ProbeJobRepository extends CrudRepository<ProbeJobMessage, String> {
}