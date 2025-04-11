package com._42195km.msa.achievementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com._42195km.msa.achievementservice", "com._42195km.msa.common"})
public class AchievementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AchievementServiceApplication.class, args);
	}

}
