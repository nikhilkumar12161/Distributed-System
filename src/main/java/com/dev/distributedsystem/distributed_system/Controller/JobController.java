package com.dev.distributedsystem.distributed_system.Controller;

import com.dev.distributedsystem.distributed_system.Model.Job;
import com.dev.distributedsystem.distributed_system.Model.JobStatus;
import com.dev.distributedsystem.distributed_system.Model.JobType;
import com.dev.distributedsystem.distributed_system.Repositories.JobRepository;
import io.micrometer.observation.ObservationPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Predicate;

@Controller
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/")
    @ResponseBody
    public HashMap<String, String> helloWorld(){
        HashMap<String, String> map = new HashMap<>();
        map.put("value","Hello world");
        return map;

    }

    @PostMapping("/add-job")
    @ResponseBody
    public List<Job> addJob(@RequestBody RequestBody responseBody){
        return jobRepository.findAll();
    }

    @GetMapping("/get-all-jobs")
    @ResponseBody
    public List<Job> getJobByStatus(){
        List<Job> jobs = jobRepository.findAll();
        return jobs;
    }

    @GetMapping("/get-jobs-by-status")
    @ResponseBody
    public List<Job> getJobByStatus(@RequestParam JobStatus status){
        List<Job> jobs = jobRepository.findJobsByStatus(status);
        return jobs;
    }

    @GetMapping("/get-jobs-by-type")
    @ResponseBody
    public List<Job> getJobByType(@RequestParam JobType type){
        List<Job> jobs = jobRepository.findJobsByType(type);
        return jobs;
    }
}
