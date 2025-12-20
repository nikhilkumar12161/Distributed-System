package com.dev.distributedsystem.distributed_system.Model;

public enum JobStatus {
    CREATED,
    SCHEDULED,
    ENQUEUED,
    RUNNING,
    SUCCESS,
    FAILED,
    DLQ
}
