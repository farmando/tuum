package com.tuum.interview;

import com.tuum.interview.controller.AccountController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;



@Testcontainers
@SpringBootTest
class TuumInterviewApplicationTests {

    @Autowired
    private AccountController accountController;

    final static DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:13.2-alpine");

    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("tuum_db")
            .withUsername("root")
            .withPassword("root")
            .withInitScript("database.sql");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    void contextLoads() {
        assertThat(accountController).isNotNull();
    }
}
