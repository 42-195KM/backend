package com._42195km.msa.competitionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com._42195km.msa.competitionservice", "com._42195km.msa.common"})
public class CompetitionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompetitionServiceApplication.class, args);
	}

}
