package com.dev.distributedsystem.distributed_system.Controller.dto;

import com.dev.distributedsystem.distributed_system.Model.JobType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
public class CreateJobRequest {
    private JobType type;
    private String payload;
    private Instant runAt;
    private int maxAttempts;
    private int priority;
}
