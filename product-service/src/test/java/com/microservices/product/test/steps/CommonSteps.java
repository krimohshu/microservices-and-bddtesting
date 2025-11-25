package com.microservices.product.test.steps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.microservices.product.test.context.TestContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Common Step Definitions shared across V1 and V2 features
 */
@Slf4j
@RequiredArgsConstructor
public class CommonSteps {
    
    private final TestContext testContext;
    
    @Given("the Product Service V1 is running")
    public void theProductServiceV1IsRunning() {
        String baseUrl = (String) testContext.getData("baseUrl");
        RestAssured.baseURI = baseUrl;
        log.info("Product Service V1 is running at: {}", baseUrl);
    }
    
    @Given("the Product Service V2 is running")
    public void theProductServiceV2IsRunning() {
        String baseUrl = (String) testContext.getData("baseUrl");
        RestAssured.baseURI = baseUrl;
        log.info("Product Service V2 is running at: {}", baseUrl);
    }
    
    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        endpoint = replacePathVariables(endpoint);
        log.info("Sending GET request to: {}", endpoint);
        
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get(endpoint);
        
        testContext.setResponse(response);
        log.info("Response status: {}", response.getStatusCode());
    }
    
    @When("I send a POST request to {string} with body:")
    public void iSendAPostRequestToWithBody(String endpoint, String body) {
        endpoint = replacePathVariables(endpoint);
        body = replaceBodyVariables(body);
        log.info("Sending POST request to: {} with body: {}", endpoint, body);
        
        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(endpoint);
        
        testContext.setResponse(response);
        log.info("Response status: {}", response.getStatusCode());
        log.debug("Response body: {}", response.getBody().asString());
    }
    
    @When("I send a PUT request to {string} with body:")
    public void iSendAPutRequestToWithBody(String endpoint, String body) {
        endpoint = replacePathVariables(endpoint);
        body = replaceBodyVariables(body);
        log.info("Sending PUT request to: {} with body: {}", endpoint, body);
        
        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put(endpoint);
        
        testContext.setResponse(response);
        log.info("Response status: {}", response.getStatusCode());
    }
    
    @When("I send a DELETE request to {string}")
    public void iSendADeleteRequestTo(String endpoint) {
        endpoint = replacePathVariables(endpoint);
        log.info("Sending DELETE request to: {}", endpoint);
        
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .delete(endpoint);
        
        testContext.setResponse(response);
        log.info("Response status: {}", response.getStatusCode());
    }
    
    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        Response response = testContext.getResponse();
        assertThat("Response status code", response.getStatusCode(), equalTo(expectedStatus));
        log.info("Verified response status: {}", expectedStatus);
    }
    
    @Then("the response should contain:")
    public void theResponseShouldContain(Map<String, String> expectedData) {
        Response response = testContext.getResponse();
        
        expectedData.forEach((field, expectedValue) -> {
            String actualValue = response.jsonPath().getString(field);
            assertThat(String.format("Field '%s'", field), actualValue, equalTo(expectedValue));
            log.info("Verified field '{}' = '{}'", field, expectedValue);
        });
    }
    
    @Then("the response should have field {string}")
    public void theResponseShouldHaveField(String fieldName) {
        Response response = testContext.getResponse();
        Object fieldValue = response.jsonPath().get(fieldName);
        assertThat(String.format("Field '%s' should exist", fieldName), fieldValue, notNullValue());
        log.info("Verified field '{}' exists with value: {}", fieldName, fieldValue);
    }
    
    @Then("the response should be a list")
    public void theResponseShouldBeAList() {
        Response response = testContext.getResponse();
        List<?> list = response.jsonPath().getList("$");
        assertThat("Response should be a list", list, notNullValue());
        log.info("Verified response is a list with {} items", list.size());
    }
    
    @Then("the response list should have at least {int} items")
    public void theResponseListShouldHaveAtLeastItems(int minItems) {
        Response response = testContext.getResponse();
        List<?> list = response.jsonPath().getList("$");
        assertThat("List size", list.size(), greaterThanOrEqualTo(minItems));
        log.info("Verified list has at least {} items (actual: {})", minItems, list.size());
    }
    
    @Then("the response list should have {int} items")
    public void theResponseListShouldHaveItems(int expectedItems) {
        Response response = testContext.getResponse();
        List<?> list = response.jsonPath().getList("$");
        assertThat("List size", list.size(), equalTo(expectedItems));
        log.info("Verified list has exactly {} items", expectedItems);
    }
    
    @Then("the response list should contain {string}")
    public void theResponseListShouldContain(String expectedValue) {
        Response response = testContext.getResponse();
        List<String> list = response.jsonPath().getList("$");
        assertThat("List should contain value", list, hasItem(expectedValue));
        log.info("Verified list contains '{}'", expectedValue);
    }
    
    @Then("the response should not be empty")
    public void theResponseShouldNotBeEmpty() {
        Response response = testContext.getResponse();
        String body = response.getBody().asString();
        assertThat("Response body should not be empty", body, not(emptyString()));
        log.info("Verified response is not empty");
    }
    
    @Then("the response field {string} should match pattern {string}")
    public void theResponseFieldShouldMatchPattern(String fieldName, String patternString) {
        Response response = testContext.getResponse();
        String fieldValue = response.jsonPath().getString(fieldName);
        Pattern pattern = Pattern.compile(patternString);
        assertThat(String.format("Field '%s' should match pattern", fieldName), 
                   pattern.matcher(fieldValue).matches(), is(true));
        log.info("Verified field '{}' matches pattern '{}'", fieldName, patternString);
    }
    
    @Then("the response time should be less than {int} ms")
    public void theResponseTimeShouldBeLessThanMs(int maxTimeMs) {
        Response response = testContext.getResponse();
        long responseTime = response.getTime();
        assertThat("Response time", responseTime, lessThan((long) maxTimeMs));
        log.info("Verified response time {} ms < {} ms", responseTime, maxTimeMs);
    }
    
    // Helper methods
    
    private String replacePathVariables(String endpoint) {
        Map<String, Object> allData = testContext.getAllData();
        String result = endpoint;
        
        for (Map.Entry<String, Object> entry : allData.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            if (result.contains(placeholder)) {
                result = result.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        
        return result;
    }
    
    private String replaceBodyVariables(String body) {
        Map<String, Object> allData = testContext.getAllData();
        String result = body;
        
        for (Map.Entry<String, Object> entry : allData.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            if (result.contains(placeholder)) {
                result = result.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        
        return result;
    }
}
