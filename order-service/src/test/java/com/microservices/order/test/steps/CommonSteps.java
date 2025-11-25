package com.microservices.order.test.steps;

import com.microservices.order.test.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class CommonSteps {

    private final TestContext testContext;

    @Given("the API version is {string}")
    public void theAPIVersionIs(String version) {
        testContext.setApiVersion(version);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        testContext.getResponse().then().statusCode(statusCode);
    }

    @Then("the response should contain field {string}")
    public void theResponseShouldContainField(String fieldName) {
        testContext.getResponse().then().body(fieldName, notNullValue());
    }

    @Then("the response field {string} should be {string}")
    public void theResponseFieldShouldBe(String fieldName, String expectedValue) {
        testContext.getResponse().then().body(fieldName, equalTo(expectedValue));
    }
}
