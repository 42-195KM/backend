package com._42195km.msa.crew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com._42195km.msa.crew", "com._42195km.msa.common"})
public class CrewApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrewApplication.class, args);
	}

}
