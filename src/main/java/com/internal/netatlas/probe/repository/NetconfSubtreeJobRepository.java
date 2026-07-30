package com.internal.netatlas.probe.repository;

import org.springframework.stereotype.Repository;

@Repository
public class NetconfSubtreeJobRepository {

    // In a real system this would be a CassandraRepository; here we just log actions
    public void saveJob(String jobId, String deviceId, String status) {
        // Simple placeholder implementation
        System.out.printf("NetconfSubtreeJob saved: jobId=%s, deviceId=%s, status=%s%n",
                jobId, deviceId, status);
    }
}
