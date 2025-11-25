package com.microservices.order.contract.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * CONSUMER PACT TEST
 * 
 * This test runs in ORDER SERVICE (the Consumer)
 * Purpose: Define what Order Service EXPECTS from User Service
 * 
 * What happens when this test runs:
 * 1. Pact starts a MOCK User Service on a random port
 * 2. Test makes real HTTP call to this mock service
 * 3. Mock service responds with what we defined
 * 4. Test verifies the response is what Order Service needs
 * 5. Pact generates a CONTRACT file (JSON) in target/pacts/
 * 
 * Key Point: User Service doesn't need to be running!
 */
@ExtendWith(PactConsumerTestExt.class)  // Enables Pact test framework
@SpringBootTest                          // Loads Spring context (optional for this test)
@PactTestFor(
    providerName = "user-service",       // Name of the provider we're testing against
    pactVersion = PactSpecVersion.V4     // Use Pact spec version 4
)
public class UserServiceConsumerPactTest {

    /**
     * PACT DEFINITION 1: Get User By ID - Success Case
     * 
     * This method defines a CONTRACT:
     * "When Order Service calls GET /api/v1/users/{id}, 
     *  User Service should respond with this JSON structure"
     */
    @Pact(consumer = "order-service", provider = "user-service")
    public V4Pact getUserByIdPact(PactDslWithProvider builder) {
        
        // Define the expected response body structure
        DslPart expectedResponseBody = new PactDslJsonBody()
                // stringType() means: field must be string, but value can vary
                .stringType("id", "1")                    // User ID
                .stringType("name", "John Doe")           // User name
                .stringType("email", "john@example.com")  // Email
                .stringType("status", "ACTIVE")           // Account status
                // Regex pattern for ISO date format
                .stringMatcher("createdAt", 
                    "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*", 
                    "2024-01-01T10:00:00");

        // Define HTTP headers
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");

        // Build the pact (contract)
        return builder
                // Provider State: Setup condition in provider
                .given("user with id 1 exists")
                
                // Request definition
                .uponReceiving("a request to get user by id")
                    .path("/api/v1/users/1")           // API endpoint
                    .method("GET")                      // HTTP method
                
                // Expected response
                .willRespondWith()
                    .status(200)                        // HTTP 200 OK
                    .headers(responseHeaders)           // Content-Type header
                    .body(expectedResponseBody)         // JSON response body
                
                .toPact(V4Pact.class);                 // Create Pact object
    }

    /**
     * TEST 1: Verify Order Service can call User Service successfully
     * 
     * This test:
     * 1. Gets mock server URL from Pact
     * 2. Makes REAL HTTP call using RestTemplate
     * 3. Verifies response matches Order Service expectations
     * 
     * If this test passes, a pact file is generated:
     * target/pacts/order-service-user-service.json
     */
    @Test
    @PactTestFor(pactMethod = "getUserByIdPact")
    void testGetUserById_shouldReturnUserDetails(
            au.com.dius.pact.consumer.MockServer mockServer) {
        
        System.out.println("🔵 TEST: Order Service calling User Service");
        System.out.println("🔵 Mock Server URL: " + mockServer.getUrl());
        
        // Arrange: Create REST client
        RestTemplate restTemplate = new RestTemplate();
        String url = mockServer.getUrl() + "/api/v1/users/1";
        
        System.out.println("🔵 Calling: " + url);
        
        // Act: Make HTTP call to mock User Service
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        System.out.println("🔵 Response Status: " + response.getStatusCode());
        System.out.println("🔵 Response Body: " + response.getBody());
        
        // Assert: Verify response is what Order Service expects
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("id")).isEqualTo("1");
        assertThat(response.getBody().get("name")).isEqualTo("John Doe");
        assertThat(response.getBody().get("email")).isEqualTo("john@example.com");
        assertThat(response.getBody().get("status")).isEqualTo("ACTIVE");
        assertThat(response.getBody().get("createdAt")).isNotNull();
        
        System.out.println("✅ TEST PASSED: Order Service got expected user data");
        System.out.println("✅ Pact file will be generated in target/pacts/");
    }

    /**
     * PACT DEFINITION 2: Get User By ID - Not Found Case
     * 
     * This defines another interaction:
     * "When user doesn't exist, User Service should return 404"
     */
    @Pact(consumer = "order-service", provider = "user-service")
    public V4Pact getUserNotFoundPact(PactDslWithProvider builder) {
        
        // Define error response body
        DslPart errorResponseBody = new PactDslJsonBody()
                .stringType("error", "User not found")
                .stringType("message", "User with id 999 does not exist")
                .integerType("status", 404);

        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");

        return builder
                // Provider State: User doesn't exist
                .given("user with id 999 does not exist")
                
                .uponReceiving("a request to get non-existent user")
                    .path("/api/v1/users/999")
                    .method("GET")
                
                .willRespondWith()
                    .status(404)                        // HTTP 404 Not Found
                    .headers(responseHeaders)
                    .body(errorResponseBody)
                
                .toPact(V4Pact.class);
    }

    /**
     * TEST 2: Verify Order Service handles 404 correctly
     */
    @Test
    @PactTestFor(pactMethod = "getUserNotFoundPact")
    void testGetUserById_shouldReturn404ForNonExistentUser(
            au.com.dius.pact.consumer.MockServer mockServer) {
        
        System.out.println("🔵 TEST: Order Service calling User Service (404 case)");
        
        // Arrange
        RestTemplate restTemplate = new RestTemplate();
        String url = mockServer.getUrl() + "/api/v1/users/999";
        
        System.out.println("🔵 Calling: " + url);
        
        // Act & Assert: Should throw exception for 404
        try {
            restTemplate.getForEntity(url, Map.class);
            fail("Should have thrown HttpClientErrorException");
        } catch (HttpClientErrorException e) {
            System.out.println("🔵 Got expected 404 error");
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            System.out.println("✅ TEST PASSED: Order Service handles 404 correctly");
        }
    }
}
