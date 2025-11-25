package com.api.springboot.test.steps;

import com.api.springboot.test.context.TestContext;
import io.cucumber.java.en.Given;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RequiredArgsConstructor
public class GivenSteps {
    
    private final TestContext testContext;
    
    @Given("the API is running")
    public void theApiIsRunning() {
        // Verify API is accessible - don't store response
        given()
            .when()
            .get()
            .then()
            .statusCode(200);
        // Don't set testContext.setResponse() here
    }
    
    @Given("an object with name {string} exists")
    public void anObjectWithNameExists(String name) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("data", Map.of("test", "data"));
        
        var response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post();
        
        Long id = ((Number) response.path("id")).longValue();
        testContext.setCurrentObjectId(id);
        testContext.addCreatedObjectId(id);
        testContext.setCurrentObjectData(Map.of("test", "data"));
        // Don't set response here - let the When step set it
    }
    
    @Given("the following objects exist:")
    public void theFollowingObjectsExist(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", row.get("name"));
            requestBody.put("data", parseJsonString(row.get("data")));
            
            var response = given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                .when()
                    .post();
            
            Long id = ((Number) response.path("id")).longValue();
            testContext.addCreatedObjectId(id);
        }
        // Don't set response here - let the When step set it
    }
    
    @Given("the API has no objects")
    public void theApiHasNoObjects() {
        // Clean up all objects before test
        testContext.setResponse(
            given()
            .when()
                .get()
        );
        
        List<Integer> ids = testContext.getResponse().path("id");
        if (ids != null) {
            ids.forEach(id -> {
                given()
                .when()
                    .delete("/" + id);
            });
        }
    }
    
    private Map<String, Object> parseJsonString(String json) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(json, Map.class);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
