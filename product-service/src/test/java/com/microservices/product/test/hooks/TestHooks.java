package com.microservices.product.test.hooks;

import org.springframework.boot.test.web.server.LocalServerPort;

import com.microservices.product.test.context.TestContext;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Test Hooks for Cucumber scenarios
 * Handles setup and teardown operations
 */
@Slf4j
@RequiredArgsConstructor
public class TestHooks {
    
    private final TestContext testContext;
    
    @LocalServerPort
    private int port;
    
    @Before
    public void beforeScenario(Scenario scenario) {
        log.info("========================================");
        log.info("Starting scenario: {}", scenario.getName());
        log.info("Tags: {}", scenario.getSourceTagNames());
        log.info("Server running on port: {}", port);
        log.info("========================================");
        
        // Store the base URL in test context
        testContext.setData("baseUrl", "http://localhost:" + port);
    }
    
    @After
    public void afterScenario(Scenario scenario) {
        log.info("========================================");
        log.info("Finished scenario: {}", scenario.getName());
        log.info("Status: {}", scenario.getStatus());
        
        if (scenario.isFailed()) {
            log.error("Scenario FAILED: {}", scenario.getName());
            
            // Log response details if available
            if (testContext.getResponse() != null) {
                log.error("Response Status: {}", testContext.getResponse().getStatusCode());
                log.error("Response Body: {}", testContext.getResponse().getBody().asString());
            }
        }
        
        log.info("========================================");
        
        // Clean up test context
        testContext.clear();
    }
}
