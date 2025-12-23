package com.dev.distributedsystem.distributed_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DistributedSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedSystemApplication.class, args);
	}

}
