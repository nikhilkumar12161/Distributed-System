package com.dev.distributedsystem.distributed_system.Controller;

import com.dev.distributedsystem.distributed_system.Controller.dto.CreateJobRequest;
import com.dev.distributedsystem.distributed_system.Model.Job;
import com.dev.distributedsystem.distributed_system.Model.JobStatus;
import com.dev.distributedsystem.distributed_system.Model.JobType;
import com.dev.distributedsystem.distributed_system.Service.JobService;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import java.util.*;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private static final Logger logger = LoggerFactory.getLogger(JobController.class);
    private final JobService jobService;

    public JobController(JobService jobService){
        this.jobService = jobService;
    }

    @GetMapping("test-api")
    public HashMap<String, String> helloWorld() {
        logger.info("Hello world  : {}", "Nikhil");
        HashMap<String, String> map = new HashMap<>();
        map.put("value", "Hello world");
        return map;
    }

    @PostMapping("/add-job")
    public Job addJob(@RequestBody(required = true) CreateJobRequest request) {
        logger.info("Enter add Job Method");
        return jobService.addJob(request);
    }

    @GetMapping
    public List<Job> getJobs(
            @RequestParam(required = false) JobType type,
            @RequestParam(required = false) JobStatus status
    ) {
        logger.info("Entered getJobs method");
        return jobService.getJobs(type,status);
    }

//    @GetMapping("delete")
//    public List<Job> deleteJobs(
//            @RequestParam(required = false) JobType type,
//            @RequestParam(required = false) JobStatus status
//    ) {
//        logger.info("Entered getJobByStatus method");
//        return jobService.getJobs(type,status);
//    }
    @GetMapping("update")
    public HashMap<String, Object> updateAll(@RequestParam JobStatus status){
        return jobService.updateStatus(status);
    }
}
