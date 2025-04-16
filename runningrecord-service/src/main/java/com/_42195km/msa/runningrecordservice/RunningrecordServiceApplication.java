package com._42195km.msa.runningrecordservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com._42195km.msa.runningrecordservice", "com._42195km.msa.common"})
@EnableFeignClients
@EnableDiscoveryClient
public class RunningrecordServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunningrecordServiceApplication.class, args);
    }

}
