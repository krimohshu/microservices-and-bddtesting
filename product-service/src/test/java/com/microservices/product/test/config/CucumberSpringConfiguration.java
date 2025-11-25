package com.microservices.product.test.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.cucumber.spring.CucumberContextConfiguration;

/**
 * Cucumber Spring Configuration
 * Bootstraps Spring Boot context for Cucumber tests
 */
@CucumberContextConfiguration
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.h2.console.enabled=false"
    }
)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    // This class serves as the bridge between Cucumber and Spring Boot
    // Spring Boot will automatically scan and load all components
}
