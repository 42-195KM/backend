package com._42195km.msa.userrecapservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com._42195km.msa.userrecapservice", "com._42195km.msa.common"})
@EnableFeignClients
public class UserrecapServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserrecapServiceApplication.class, args);
	}

}
