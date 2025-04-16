package com._42195km.msa.runningrecordservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com._42195km.msa.runningrecordservice", "com._42195km.msa.common"})
public class RunningrecordServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunningrecordServiceApplication.class, args);
    }

}
