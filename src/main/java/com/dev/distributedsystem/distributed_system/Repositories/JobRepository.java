package com.dev.distributedsystem.distributed_system.Repositories;

import com.dev.distributedsystem.distributed_system.Model.Job;
import com.dev.distributedsystem.distributed_system.Model.JobStatus;
import com.dev.distributedsystem.distributed_system.Model.JobType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findJobsByStatus(JobStatus jobStatus);
    List<Job> findJobsByType(JobType jobType);
}
