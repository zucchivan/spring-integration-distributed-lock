package com.zucchivan.distributedlockpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@EnableIntegration
@SpringBootApplication
public class DistributedLockApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedLockApplication.class, args);
	}

}
