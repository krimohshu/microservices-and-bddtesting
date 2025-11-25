package com.api.springboot.test.steps;

import com.api.springboot.test.context.TestContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;

@RequiredArgsConstructor
public class WhenSteps {
    
    private final TestContext testContext;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @When("I create an object with name {string}")
    public void iCreateAnObjectWithName(String name) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("data", Map.of("test", "data"));
        
        testContext.setResponse(
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post()
        );
        
        if (testContext.getResponse().statusCode() == 201) {
            Long id = ((Number) testContext.getResponse().path("id")).longValue();
            testContext.setCurrentObjectId(id);
            testContext.addCreatedObjectId(id);
            testContext.setCurrentObjectData(Map.of("test", "data"));
        }
    }
    
    @When("I create an object with name {string} and data:")
    public void iCreateAnObjectWithNameAndData(String name, String dataJson) {
        try {
            Map<String, Object> data = objectMapper.readValue(dataJson, Map.class);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", name);
            requestBody.put("data", data);
            
            testContext.setResponse(
                given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                .when()
                    .post()
            );
            
            if (testContext.getResponse().statusCode() == 201) {
                Long id = ((Number) testContext.getResponse().path("id")).longValue();
                testContext.setCurrentObjectId(id);
                testContext.addCreatedObjectId(id);
                testContext.setCurrentObjectData(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create object", e);
        }
    }
    
    @When("I request all objects")
    public void iRequestAllObjects() {
        testContext.setResponse(
            given()
            .when()
                .get()
        );
    }
    
    @When("I request the object by its ID")
    public void iRequestTheObjectByItsId() {
        testContext.setResponse(
            given()
            .when()
                .get("/" + testContext.getCurrentObjectId())
        );
    }
    
    @When("I request an object with id {long}")
    public void iRequestAnObjectWithId(Long id) {
        testContext.setResponse(
            given()
            .when()
                .get("/" + id)
        );
    }
    
    @When("I update the object with name {string}")
    public void iUpdateTheObjectWithName(String newName) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", newName);
        // Preserve original data if available, otherwise use default
        Object data = testContext.getCurrentObjectData();
        if (data == null) {
            data = Map.of("test", "data");
        }
        requestBody.put("data", data);
        
        testContext.setResponse(
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .put("/" + testContext.getCurrentObjectId())
        );
    }
    
    @When("I delete the object")
    public void iDeleteTheObject() {
        testContext.setResponse(
            given()
            .when()
                .delete("/" + testContext.getCurrentObjectId())
        );
    }
    
    @When("I create an object with empty name")
    public void iCreateAnObjectWithEmptyName() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "");
        requestBody.put("data", Map.of("test", "data"));
        
        testContext.setResponse(
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post()
        );
    }
    
    @When("I search for objects with name {string}")
    public void iSearchForObjectsWithName(String name) {
        testContext.setResponse(
            given()
                .queryParam("name", name)
            .when()
                .get("/search")
        );
    }
    
    @When("I retrieve the created object")
    public void iRetrieveTheCreatedObject() {
        // Store the current object data before retrieval for comparison
        Map<String, Object> originalData = testContext.getCurrentObjectData();
        
        testContext.setResponse(
            given()
            .when()
                .get("/" + testContext.getCurrentObjectId())
        );
        
        // Restore the original data for comparison in Then steps
        testContext.setCurrentObjectData(originalData);
    }
    
    @When("I create {int} objects concurrently")
    public void iCreateObjectsConcurrently(int count) {
        ExecutorService executor = Executors.newFixedThreadPool(count);
        List<CompletableFuture<Response>> futures = new ArrayList<>();
        
        IntStream.range(0, count).forEach(i -> {
            CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("name", "Concurrent Object " + i);
                requestBody.put("data", Map.of("index", i));
                
                return given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                .when()
                    .post();
            }, executor);
            futures.add(future);
        });
        
        // Wait for all requests to complete
        List<Response> responses = futures.stream()
            .map(CompletableFuture::join)
            .toList();
        
        executor.shutdown();
        
        // Store responses
        testContext.storeData("concurrentResponses", responses);
        
        // Store created IDs for cleanup
        responses.forEach(response -> {
            if (response.statusCode() == 201) {
                Long id = ((Number) response.path("id")).longValue();
                testContext.addCreatedObjectId(id);
            }
        });
    }
    
    @When("I try to update object with id {long}")
    public void iTryToUpdateObjectWithId(Long id) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Updated");
        
        testContext.setResponse(
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .put("/" + id)
        );
    }
    
    @When("I try to delete object with id {long}")
    public void iTryToDeleteObjectWithId(Long id) {
        testContext.setResponse(
            given()
            .when()
                .delete("/" + id)
        );
    }
    
    @When("I create {int} objects")
    public void iCreateObjects(int count) {
        IntStream.range(0, count).forEach(i -> {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", "Object " + i);
            requestBody.put("data", Map.of("index", i));
            
            Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post();
            
            if (response.statusCode() == 201) {
                Long id = ((Number) response.path("id")).longValue();
                testContext.addCreatedObjectId(id);
            }
        });
    }
}
