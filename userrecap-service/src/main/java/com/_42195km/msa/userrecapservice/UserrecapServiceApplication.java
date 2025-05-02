package com._42195km.msa.userrecapservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com._42195km.msa.userrecapservice", "com._42195km.msa.common"})
@EnableFeignClients
@EnableScheduling
public class UserrecapServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserrecapServiceApplication.class, args);
	}

}
