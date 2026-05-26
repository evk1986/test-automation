package com.internal.netatlas.orchestrate.repository;

import com.internal.netatlas.orchestrate.job.DailySweepJob;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CronOverlapGuardRepository extends CrudRepository<DailySweepJob, Long> {

    DailySweepJob findTopByOrderByCompletionTimeDesc();
}