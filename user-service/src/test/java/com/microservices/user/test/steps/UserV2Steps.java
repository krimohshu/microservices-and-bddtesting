package com.microservices.user.test.steps;

import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import com.microservices.user.test.context.TestContext;

@RequiredArgsConstructor
public class UserV2Steps {

    private final TestContext testContext;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("I have a valid V2 user request with role {string} and status {string}")
    public void iHaveAValidV2UserRequestWithRoleAndStatus(String role, String status) {
        String uniqueId = System.currentTimeMillis() + "-" + Thread.currentThread().getId() + "-" + (int)(Math.random() * 10000);
        Map<String, Object> request = new HashMap<>();
        request.put("username", "testuser" + uniqueId);
        request.put("email", "testuser" + uniqueId + "@example.com");
        request.put("firstName", "Test");
        request.put("lastName", "User");
        request.put("phone", "1234567890");
        request.put("role", role);
        request.put("status", status);
        request.put("active", true);
        
        testContext.setTestData("userRequest", request);
    }

    @When("I create a new V2 user")
    public void iCreateANewV2User() {
        @SuppressWarnings("unchecked")
        Map<String, Object> request = (Map<String, Object>) testContext.getTestData("userRequest");
        
        testContext.setResponse(
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v2/users")
        );

        if (testContext.getResponse().getStatusCode() == 201) {
            Long userId = testContext.getResponse().jsonPath().getLong("id");
            testContext.setCreatedUserId(userId);
            String email = testContext.getResponse().jsonPath().getString("email");
            testContext.setTestData("userEmail", email);
        }
    }

    @When("I search V2 users with pagination page {int} size {int}")
    public void iSearchV2UsersWithPagination(int page, int size) {
        Map<String, Object> request = new HashMap<>();
        request.put("page", page);
        request.put("size", size);
        
        testContext.setResponse(
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v2/users/search")
        );
    }

    @When("I search V2 users with filter role {string}")
    public void iSearchV2UsersWithFilterRole(String role) {
        Map<String, Object> request = new HashMap<>();
        request.put("role", role);
        request.put("page", 0);
        request.put("size", 10);
        
        testContext.setResponse(
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v2/users/search")
        );
    }

    @When("I search V2 users with filter status {string}")
    public void iSearchV2UsersWithFilterStatus(String status) {
        Map<String, Object> request = new HashMap<>();
        request.put("status", status);
        request.put("page", 0);
        request.put("size", 10);
        
        testContext.setResponse(
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v2/users/search")
        );
    }

    @When("I get V2 user by email")
    public void iGetV2UserByEmail() {
        String email = (String) testContext.getTestData("userEmail");
        
        testContext.setResponse(
            RestAssured.given()
                .when()
                .get("/api/v2/users/email/" + email)
        );
    }

    @When("I bulk create {int} users")
    public void iBulkCreateUsers(int count) {
        List<Map<String, Object>> users = new ArrayList<>();
        String baseId = System.currentTimeMillis() + "-" + Thread.currentThread().getId();
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> user = new HashMap<>();
            user.put("username", "bulkuser" + baseId + "-" + i);
            user.put("email", "bulk" + baseId + "-" + i + "@example.com");
            user.put("firstName", "Bulk");
            user.put("lastName", "User" + i);
            user.put("role", "USER");
            user.put("status", "ACTIVE");
            users.add(user);
        }
        
        Map<String, Object> request = new HashMap<>();
        request.put("users", users);
        
        testContext.setResponse(
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v2/users/bulk")
        );
    }

    @When("I update V2 user status to {string}")
    public void iUpdateV2UserStatusTo(String status) {
        Long userId = testContext.getCreatedUserId();
        
        testContext.setResponse(
            RestAssured.given()
                .queryParam("status", status)
                .when()
                .put("/api/v2/users/" + userId + "/status")
        );
    }

    @When("I get all V2 roles")
    public void iGetAllV2Roles() {
        testContext.setResponse(
            RestAssured.given()
                .when()
                .get("/api/v2/users/roles")
        );
    }

    @When("I get V2 user statistics")
    public void iGetV2UserStatistics() {
        testContext.setResponse(
            RestAssured.given()
                .when()
                .get("/api/v2/users/stats")
        );
    }

    @When("I generate username with firstName {string} and lastName {string}")
    public void iGenerateUsernameWithFirstNameAndLastName(String firstName, String lastName) {
        testContext.setResponse(
            RestAssured.given()
                .queryParam("firstName", firstName)
                .queryParam("lastName", lastName)
                .when()
                .post("/api/v2/users/generate-username")
        );
    }

    @When("I soft delete V2 user")
    public void iSoftDeleteV2User() {
        Long userId = testContext.getCreatedUserId();
        
        testContext.setResponse(
            RestAssured.given()
                .when()
                .delete("/api/v2/users/" + userId)
        );
    }

    @And("the V2 response should have pagination info")
    public void theV2ResponseShouldHavePaginationInfo() {
        testContext.getResponse().then()
            .body("page", notNullValue())
            .body("size", notNullValue())
            .body("totalElements", notNullValue())
            .body("totalPages", notNullValue())
            .body("content", notNullValue());
    }

    @And("the V2 response field {string} should be {string}")
    public void theV2ResponseFieldShouldBe(String fieldName, String expectedValue) {
        testContext.getResponse().then()
            .body(fieldName, equalTo(expectedValue));
    }

    @Given("I have a duplicate email user request")
    public void iHaveADuplicateEmailUserRequest() {
        String existingEmail = (String) testContext.getTestData("userEmail");
        
        Map<String, Object> request = new HashMap<>();
        request.put("username", "differentuser" + System.currentTimeMillis());
        request.put("email", existingEmail);
        request.put("firstName", "Different");
        request.put("lastName", "User");
        
        testContext.setTestData("userRequest", request);
    }

    @When("I search V2 users with invalid page {int}")
    public void iSearchV2UsersWithInvalidPage(int page) {
        Map<String, Object> request = new HashMap<>();
        request.put("page", page);
        request.put("size", 10);
        
        testContext.setResponse(
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v2/users/search")
        );
    }
}
