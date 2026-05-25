package com.performance.appraisal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class AppraisalApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppraisalApplication.class, args);
    }
}
