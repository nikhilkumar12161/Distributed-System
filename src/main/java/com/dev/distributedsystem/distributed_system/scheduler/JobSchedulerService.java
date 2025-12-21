package com.dev.distributedsystem.distributed_system.scheduler;

import com.dev.distributedsystem.distributed_system.Model.Job;
import com.dev.distributedsystem.distributed_system.Repositories.JobRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.PageRanges;
import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobSchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(JobSchedulerService.class);
    private final JobRepository jobRepository;
    public JobSchedulerService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void ScheduleJobs() throws InterruptedException {
        List<String> nums = new ArrayList<>();
        List<Job> dueJobs = jobRepository.findDueJobs();
        for(Job job : dueJobs){
            int id = jobRepository.markScheduled(job.getId());
            if(id == 1){
                // enqueued
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void work(){
        int picked = jobRepository.pickJobForExecution();
        if(picked == 0){
            return;
        }
        Job job = jobRepository.findCurrentlyRunningJob().orElseThrow();
        System.out.println(job.getPayload());
        jobRepository.completeJob(job.getId());
    }
}