package com.microservices.user.test.steps;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import com.microservices.user.test.context.TestContext;

@RequiredArgsConstructor
public class UserV1Steps {

    private final TestContext testContext;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("I have a valid user request")
    public void iHaveAValidUserRequest() {
        String uniqueId = System.currentTimeMillis() + "-" + Thread.currentThread().getId() + "-" + (int)(Math.random() * 10000);
        Map<String, Object> request = new HashMap<>();
        request.put("username", "testuser" + uniqueId);
        request.put("email", "testuser" + uniqueId + "@example.com");
        request.put("firstName", "Test");
        request.put("lastName", "User");
        request.put("phone", "1234567890");
        
        testContext.setTestData("userRequest", request);
    }

    @When("I create a new user")
    public void iCreateANewUser() {
        @SuppressWarnings("unchecked")
        Map<String, Object> request = (Map<String, Object>) testContext.getTestData("userRequest");
        
        testContext.setResponse(
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/users")
        );

        if (testContext.getResponse().getStatusCode() == 201) {
            Long userId = testContext.getResponse().jsonPath().getLong("id");
            testContext.setCreatedUserId(userId);
        }
    }

    @When("I get user by ID")
    public void iGetUserById() {
        Long userId = testContext.getCreatedUserId();
        
        testContext.setResponse(
            RestAssured.given()
                .when()
                .get("/api/v1/users/" + userId)
        );
    }

    @When("I get all users")
    public void iGetAllUsers() {
        testContext.setResponse(
            RestAssured.given()
                .when()
                .get("/api/v1/users")
        );
    }

    @When("I update the user")
    public void iUpdateTheUser() {
        Long userId = testContext.getCreatedUserId();
        
        Map<String, Object> request = new HashMap<>();
        request.put("username", "updateduser" + System.currentTimeMillis());
        request.put("email", "updated" + System.currentTimeMillis() + "@example.com");
        request.put("firstName", "Updated");
        request.put("lastName", "User");
        request.put("phone", "9876543210");
        
        testContext.setResponse(
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/v1/users/" + userId)
        );
    }

    @When("I delete the user")
    public void iDeleteTheUser() {
        Long userId = testContext.getCreatedUserId();
        
        testContext.setResponse(
            RestAssured.given()
                .when()
                .delete("/api/v1/users/" + userId)
        );
    }

    @When("I search users by username {string}")
    public void iSearchUsersByUsername(String username) {
        testContext.setResponse(
            RestAssured.given()
                .queryParam("username", username)
                .when()
                .get("/api/v1/users/search")
        );
    }

    @Given("I have an invalid user request with missing {string}")
    public void iHaveAnInvalidUserRequestWithMissing(String field) {
        Map<String, Object> request = new HashMap<>();
        request.put("username", "testuser");
        request.put("email", "test@example.com");
        request.put("firstName", "Test");
        request.put("lastName", "User");
        
        request.remove(field);
        testContext.setTestData("userRequest", request);
    }

    @When("I get user by invalid ID {int}")
    public void iGetUserByInvalidId(int id) {
        testContext.setResponse(
            RestAssured.given()
                .when()
                .get("/api/v1/users/" + id)
        );
    }

    @When("I update user with invalid ID {int}")
    public void iUpdateUserWithInvalidId(int id) {
        Map<String, Object> request = new HashMap<>();
        request.put("username", "testuser");
        request.put("email", "test@example.com");
        request.put("firstName", "Test");
        request.put("lastName", "User");
        
        testContext.setResponse(
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/v1/users/" + id)
        );
    }

    @When("I delete user with invalid ID {int}")
    public void iDeleteUserWithInvalidId(int id) {
        testContext.setResponse(
            RestAssured.given()
                .when()
                .delete("/api/v1/users/" + id)
        );
    }
}
