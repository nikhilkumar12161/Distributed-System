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
            WHERE J.status IN ('CREATED')
            AND J.runAt <= CURRENT TIMESTAMP
            AND (J.lockedUntil IS NULL OR J.lockedUntil <= CURRENT TIMESTAMP )
            ORDER BY J.priority , J.runAt ASC""")
    List<Job> findDueJobs();

    @Query("""
            SELECT J from Job J
            WHERE J.status IN ('FAILED')
            AND J.runAt <= CURRENT TIMESTAMP
            AND (J.lockedUntil IS NULL OR J.lockedUntil <= CURRENT TIMESTAMP )
            AND J.attemptCount < J.maxAttempts
            ORDER BY J.priority , J.runAt ASC""")
    List<Job> findFailedJobs();

    @Modifying
    @Query(value = """
            UPDATE jobs
            SET STATUS='SCHEDULED',
            UPDATED_AT=NOW(),
            RUN_AT=DATE_ADD(NOW(), INTERVAL 10*POW(2,ATTEMPT_COUNT) SECOND)
            WHERE ID = :jobId
            AND STATUS IN ('CREATED')
            AND ATTEMPT_COUNT < MAX_ATTEMPTS
            """, nativeQuery = true)
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
                        FOR UPDATE SKIP LOCKED
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
    int completeJob(@Param("jobId") String id);

    @Modifying
    @Query(value = """
                update jobs
                    set
                    status = case
                            when attempt_count + 1 >= max_attempts then 'DLQ'
                            else 'FAILED'
                    end,
                    updated_at = now(),
                    locked_until = null,
                    attempt_count = attempt_count + 1
                where status = 'RUNNING' 
                and id = :jobId
            """, nativeQuery = true)
    int markFailed(@Param("jobId") String id);

    @Modifying
    @Query(value = """
            UPDATE jobs
            SET STATUS='SCHEDULED',
            UPDATED_AT=NOW(),
            RUN_AT=DATE_ADD(NOW(), INTERVAL 10*POW(2,ATTEMPT_COUNT) SECOND)
            WHERE ID = :jobId
            AND STATUS IN ('FAILED')
            AND ATTEMPT_COUNT < MAX_ATTEMPTS
            """, nativeQuery = true)
    int markFailedJobsScheduled(@Param("jobId") String id);
}
