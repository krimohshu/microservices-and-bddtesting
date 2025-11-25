package com.api.springboot.test.steps;

import com.api.springboot.test.context.TestContext;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class ThenSteps {
    
    private final TestContext testContext;
    
    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        assertThat(testContext.getResponse().statusCode())
            .as("Response status code")
            .isEqualTo(statusCode);
    }
    
    @Then("the response should contain a valid object id")
    public void theResponseShouldContainAValidObjectId() {
        Long id = ((Number) testContext.getResponse().path("id")).longValue();
        assertThat(id)
            .as("Object ID")
            .isNotNull()
            .isPositive();
    }
    
    @Then("the object should have name {string}")
    public void theObjectShouldHaveName(String expectedName) {
        String actualName = testContext.getResponse().path("name");
        assertThat(actualName)
            .as("Object name")
            .isEqualTo(expectedName);
    }
    
    @Then("the response should contain at least {int} objects")
    public void theResponseShouldContainAtLeastObjects(int minCount) {
        List<Map<String, Object>> objects = testContext.getResponse().path("$");
        assertThat(objects)
            .as("Number of objects")
            .hasSizeGreaterThanOrEqualTo(minCount);
    }
    
    @Then("the object should not exist anymore")
    public void theObjectShouldNotExistAnymore() {
        Response response = given()
            .when()
            .get("/" + testContext.getCurrentObjectId());
        
        assertThat(response.statusCode())
            .as("Status code when checking deleted object")
            .isEqualTo(404);
    }
    
    @Then("the response should contain error message {string}")
    public void theResponseShouldContainErrorMessage(String expectedMessage) {
        String actualMessage = testContext.getResponse().path("message");
        assertThat(actualMessage)
            .as("Error message")
            .contains(expectedMessage);
    }
    
    @Then("the response should contain validation error")
    public void theResponseShouldContainValidationError() {
        int statusCode = testContext.getResponse().statusCode();
        assertThat(statusCode)
            .as("Status code for validation error")
            .isEqualTo(400);
        
        Map<String, Object> errors = testContext.getResponse().path("errors");
        assertThat(errors)
            .as("Validation errors")
            .isNotNull()
            .isNotEmpty();
    }
    
    @Then("the response should contain {int} objects")
    public void theResponseShouldContainObjects(int expectedCount) {
        List<Map<String, Object>> objects = testContext.getResponse().path("$");
        assertThat(objects)
            .as("Number of objects")
            .hasSize(expectedCount);
    }
    
    @Then("the data should match the original data")
    public void theDataShouldMatchTheOriginalData() {
        Map<String, Object> responseData = testContext.getResponse().path("data");
        Map<String, Object> originalData = testContext.getCurrentObjectData();
        
        assertThat(responseData)
            .as("Object data")
            .containsAllEntriesOf(originalData);
    }
    
    @SuppressWarnings("unchecked")
    @Then("all objects should be created successfully")
    public void allObjectsShouldBeCreatedSuccessfully() {
        List<Response> responses = (List<Response>) testContext.getData("concurrentResponses");
        
        assertThat(responses)
            .as("All concurrent responses")
            .allMatch(response -> response.statusCode() == 201);
    }
    
    @SuppressWarnings("unchecked")
    @Then("each object should have a unique ID")
    public void eachObjectShouldHaveAUniqueId() {
        List<Response> responses = (List<Response>) testContext.getData("concurrentResponses");
        
        Set<Long> ids = new HashSet<>();
        responses.forEach(response -> {
            Long id = ((Number) response.path("id")).longValue();
            ids.add(id);
        });
        
        assertThat(ids)
            .as("Unique IDs")
            .hasSize(responses.size());
    }
    
    @Then("the object should have createdAt timestamp")
    public void theObjectShouldHaveCreatedAtTimestamp() {
        String createdAt = testContext.getResponse().path("createdAt");
        assertThat(createdAt)
            .as("createdAt timestamp")
            .isNotNull();
        
        testContext.storeData("createdAt", createdAt);
    }
    
    @Then("the object should have updatedAt timestamp")
    public void theObjectShouldHaveUpdatedAtTimestamp() {
        String updatedAt = testContext.getResponse().path("updatedAt");
        assertThat(updatedAt)
            .as("updatedAt timestamp")
            .isNotNull();
        
        testContext.storeData("initialUpdatedAt", updatedAt);
    }
    
    @Then("the updatedAt timestamp should be different from createdAt")
    public void theUpdatedAtTimestampShouldBeDifferentFromCreatedAt() throws InterruptedException {
        // Small delay to ensure timestamp difference
        Thread.sleep(100);
        
        String newUpdatedAt = testContext.getResponse().path("updatedAt");
        String createdAt = (String) testContext.getData("createdAt");
        
        assertThat(newUpdatedAt)
            .as("Updated timestamp")
            .isNotEqualTo(createdAt);
    }
    
    @Then("the response should contain exactly {int} objects")
    public void theResponseShouldContainExactlyObjects(int expectedCount) {
        List<Map<String, Object>> objects = testContext.getResponse().path("$");
        assertThat(objects)
            .as("Exact number of objects")
            .hasSize(expectedCount);
    }
    
    @Then("the object data should preserve all data types")
    public void theObjectDataShouldPreserveAllDataTypes() {
        Map<String, Object> data = testContext.getResponse().path("data");
        
        assertThat(data)
            .as("Data object")
            .isNotNull();
        
        assertThat(data.get("string"))
            .as("String type")
            .isInstanceOf(String.class);
        
        assertThat(data.get("number"))
            .as("Number type")
            .isInstanceOf(Number.class);
        
        assertThat(data.get("boolean"))
            .as("Boolean type")
            .isInstanceOf(Boolean.class);
        
        assertThat(data.get("array"))
            .as("Array type")
            .isInstanceOf(List.class);
        
        assertThat(data.get("nested"))
            .as("Nested object type")
            .isInstanceOf(Map.class);
    }
}
