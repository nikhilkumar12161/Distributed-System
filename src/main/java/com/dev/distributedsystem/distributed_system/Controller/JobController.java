package com.dev.distributedsystem.distributed_system.Controller;

import com.dev.distributedsystem.distributed_system.Controller.dto.CreateJobRequest;
import com.dev.distributedsystem.distributed_system.Model.Job;
import com.dev.distributedsystem.distributed_system.Model.JobStatus;
import com.dev.distributedsystem.distributed_system.Model.JobType;
import com.dev.distributedsystem.distributed_system.Repositories.JobRepository;
import io.micrometer.observation.ObservationPredicate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Predicate;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private static Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/")
    public HashMap<String, String> helloWorld(){
        logger.info("Hello workd  : {}","Nikhil");
        HashMap<String, String> map = new HashMap<>();
        map.put("value","Hello world");
        return map;
    }

    @PostMapping("/add-job")
    public Job addJob(@RequestBody CreateJobRequest request){
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

    @GetMapping("/get-all-jobs")
    public List<Job> getJobByStatus(){
        return jobRepository.findAll();
    }

    @GetMapping("/get-jobs-by-status")
    public List<Job> getJobByStatus(@RequestParam JobStatus status){
        return jobRepository.findJobsByStatus(status);
    }

    @GetMapping("/get-jobs-by-type")
    public List<Job> getJobByType(@RequestParam JobType type){
        return jobRepository.findJobsByType(type);
    }

    @DeleteMapping("/delete-all-jobs")
    public List<Job> deleteAllJobs(){
        jobRepository.deleteAll();
        return new ArrayList<>();
    }
}
