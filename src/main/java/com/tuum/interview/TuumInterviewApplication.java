package com.tuum.interview;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRabbit
@EnableTransactionManagement
@MapperScan(basePackages = "com.tuum.interview.mapper")
@SpringBootApplication
public class TuumInterviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(TuumInterviewApplication.class, args);
    }

}
