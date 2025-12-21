package com.dev.distributedsystem.distributed_system.Repositories;

import com.dev.distributedsystem.distributed_system.Model.Job;
import com.dev.distributedsystem.distributed_system.Model.JobStatus;
import com.dev.distributedsystem.distributed_system.Model.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findByStatus(JobStatus jobStatus);

    List<Job> findByType(JobType jobType);

    List<Job> findByTypeAndStatus(JobType type, JobStatus status);

    List<Job> deleteJobByType(JobType type);

    List<Job> deleteJobByStatus(JobStatus status);

    List<Job> deleteJobByTypeAndStatus(JobType type, JobStatus status);

    @Modifying
    @Query(value = """
            update Job J
            set J.status = :stats, J.updatedAt = current timestamp
            """)
    int updateAllJobStatus(@Param("stats") JobStatus status);

    //    Scheduler methods
    @Query("""
            SELECT J from Job J
            WHERE J.status IN ('CREATED','FAILED')
            AND J.runAt <= CURRENT TIMESTAMP
            AND (J.lockedUntil IS NULL OR J.lockedUntil <= CURRENT TIMESTAMP )
            ORDER BY J.priority , J.runAt ASC""")
    List<Job> findDueJobs();

    @Modifying
    @Query("""
            UPDATE Job J
            SET J.status = 'SCHEDULED', J.updatedAt = current timestamp
            where J.id = :jobId
            and J.status IN ('CREATED','FAILED')
            """)
    int markScheduled(@Param("jobId") String id);

    @Modifying
    @Query(value = """
             update jobs
                set status = 'RUNNING',
                    locked_until = DATE_ADD(NOW(), INTERVAL 2 MINUTE),
                    updated_at = NOW()
                where id = (
                    select id from (
                        select id from jobs
                        where status = 'SCHEDULED' AND
                            ( locked_until is null or locked_until <= NOW())
                        order by priority, run_at desc limit 1
                    )as tmp
                )
            """, nativeQuery = true)
    int pickJobForExecution();

    @Query("""
                select J from Job J
                where J.status = 'RUNNING'
                  and J.lockedUntil > current timestamp
                  order by J.updatedAt desc
            """)
    Optional<Job> findCurrentlyRunningJob();

    @Modifying
    @Query("""
                update Job
                    set status = 'SUCCESS',
                    updatedAt = current timestamp,
                    lockedUntil = null
                where status = 'RUNNING' and
                id = :jobId
            """)
    void completeJob(@Param("jobId") String id);

}
