package com.tuum.interview.mapper;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class BaseTest {

    final static DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:13.2-alpine");

    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("tuum_db")
            .withUsername("root")
            .withPassword("root")
            .withInitScript("database.sql");

    static {
        container.start();
    }
}
