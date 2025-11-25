package com.microservices.user.test.steps;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import com.microservices.user.test.context.TestContext;

@RequiredArgsConstructor
public class CommonSteps {

    private final TestContext testContext;

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        assertEquals(statusCode, testContext.getResponse().getStatusCode(),
            "Expected status code " + statusCode + " but got " + testContext.getResponse().getStatusCode());
    }

    @And("the response should contain field {string}")
    public void theResponseShouldContainField(String fieldName) {
        testContext.getResponse().then()
            .body(fieldName, notNullValue());
    }

    @And("the response field {string} should be {string}")
    public void theResponseFieldShouldBe(String fieldName, String expectedValue) {
        testContext.getResponse().then()
            .body(fieldName, equalTo(expectedValue));
    }

    @And("the response field {string} should be {int}")
    public void theResponseFieldShouldBeInt(String fieldName, int expectedValue) {
        testContext.getResponse().then()
            .body(fieldName, equalTo(expectedValue));
    }

    @And("the response should contain error message")
    public void theResponseShouldContainErrorMessage() {
        testContext.getResponse().then()
            .body("message", notNullValue());
    }

    @And("the response should be a list")
    public void theResponseShouldBeAList() {
        testContext.getResponse().then()
            .body("$", instanceOf(java.util.List.class));
    }

    @And("the response list should have {int} items")
    public void theResponseListShouldHaveItems(int count) {
        testContext.getResponse().then()
            .body("size()", equalTo(count));
    }

    @And("the response list should not be empty")
    public void theResponseListShouldNotBeEmpty() {
        testContext.getResponse().then()
            .body("size()", greaterThan(0));
    }
}
