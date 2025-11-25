package com.api.springboot.test.hooks;

import com.api.springboot.repository.ApiObjectRepository;
import com.api.springboot.test.context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;

@RequiredArgsConstructor
public class TestHooks {
    
    @LocalServerPort
    private int port;
    
    private final TestContext testContext;
    private final ApiObjectRepository repository;
    
    @Before
    public void setUp(Scenario scenario) {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/api/objects";
        testContext.reset();
    }
    
    @After
    public void tearDown(Scenario scenario) {
        // Clean up created objects - use iterator to avoid ConcurrentModificationException
        if (!testContext.getCreatedObjectIds().isEmpty()) {
            // Create a copy of the list to avoid concurrent modification
            var idsToDelete = new java.util.ArrayList<>(testContext.getCreatedObjectIds());
            for (Long id : idsToDelete) {
                try {
                    repository.deleteById(id);
                } catch (Exception e) {
                    // Ignore if already deleted
                }
            }
            testContext.getCreatedObjectIds().clear();
        }
    }
}
