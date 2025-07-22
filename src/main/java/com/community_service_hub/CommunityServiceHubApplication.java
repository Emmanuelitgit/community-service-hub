package com.community_service_hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CommunityServiceHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityServiceHubApplication.class, args);
	}

}
