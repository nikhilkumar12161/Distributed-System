package com.dev.distributedsystem.distributed_system.Repositories;

import com.dev.distributedsystem.distributed_system.Model.Job;
import com.dev.distributedsystem.distributed_system.Model.JobStatus;
import com.dev.distributedsystem.distributed_system.Model.JobType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job,String> {
    List<Job> findByStatus(JobStatus jobStatus);
    List<Job> findByType(JobType jobType);
    List<Job> findByTypeAndStatus(JobType type, JobStatus status);
    List<Job> deleteJobByType(JobType type);
    List<Job> deleteJobByStatus(JobStatus status);
    List<Job> deleteJobByTypeAndStatus(JobType type, JobStatus status);
}
