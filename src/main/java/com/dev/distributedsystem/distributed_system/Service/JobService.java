package com.dev.distributedsystem.distributed_system.Service;

import com.dev.distributedsystem.distributed_system.Controller.dto.CreateJobRequest;
import com.dev.distributedsystem.distributed_system.Model.Job;
import com.dev.distributedsystem.distributed_system.Model.JobStatus;
import com.dev.distributedsystem.distributed_system.Model.JobType;
import com.dev.distributedsystem.distributed_system.Repositories.JobRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    public  JobService(JobRepository jobRepository){
        this.jobRepository = jobRepository;
    }

    public Job addJob(CreateJobRequest request){
        logger.info("Entered addJobs methods");
        Job job = new Job();
        job.setId(UUID.randomUUID().toString());
        job.setType(request.getType());
        job.setStatus(JobStatus.CREATED);
        job.setPayload(request.getPayload());
        job.setRunAt(request.getRunAt());
        job.setAttemptCount(0);
        job.setMaxAttempts(request.getMaxAttempts());
        job.setPriority(request.getPriority());
        return jobRepository.save(job);
    }

    public List<Job> getJobs(JobType type, JobStatus status){
        logger.info("Entered getJobs methods");
        if (type != null && status != null) {
            return jobRepository.findByTypeAndStatus(type, status);
        } else if (type != null) {
            return jobRepository.findByType(type);
        } else if(status != null){
            return jobRepository.findByStatus(status);
        } else{
            return jobRepository.findAll();
        }
    }

//    public List<Job> deleteJobs(JobType type, JobStatus status){
//        if (type != null && status != null) {
//            return jobRepository.deleteJobByTypeAndStatus(type, status);
//        } else if (type != null) {
//            return jobRepository.deleteJobByType(type);
//        } else if(status != null){
//            return jobRepository.deleteJobByStatus(status);
//        } else{
//            return jobRepository.deleteAll();
//        }
//    }
   @Transactional
   public HashMap<String, Object> updateStatus(JobStatus status) {
       HashMap<String, Object> map = new HashMap<>();
       Integer result = jobRepository.updateAllJobStatus(status);
       map.put("Rows updated", result);
       map.put("final result",jobRepository.findAll());
       return map;
   }
}
