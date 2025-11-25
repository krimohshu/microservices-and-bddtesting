package com.microservices.product.test.steps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Map;

import com.microservices.product.test.context.TestContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Step Definitions for Product Service V1 API
 */
@Slf4j
@RequiredArgsConstructor
public class ProductV1Steps {
    
    private final TestContext testContext;
    
    @Given("I create a product with name {string} and price {double}")
    public void iCreateAProductWithNameAndPrice(String name, double price) {
        String requestBody = String.format("""
            {
                "name": "%s",
                "description": "Test product",
                "price": %.2f,
                "stock": 10
            }
            """, name, price);
        
        log.info("Creating V1 product: {} with price: {}", name, price);
        
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/products");
        
        assertThat("Product creation should succeed", response.getStatusCode(), equalTo(201));
        
        Long productId = response.jsonPath().getLong("id");
        testContext.setData("productId", productId);
        testContext.setResponse(response);
        
        log.info("Created product with ID: {}", productId);
    }
    
    @Then("the response list should contain product with name {string}")
    public void theResponseListShouldContainProductWithName(String expectedName) {
        Response response = testContext.getResponse();
        List<Map<String, Object>> products = response.jsonPath().getList("$");
        
        boolean found = products.stream()
                .anyMatch(product -> expectedName.equals(product.get("name")));
        
        assertThat(String.format("Product list should contain product with name '%s'", expectedName), 
                   found, is(true));
        log.info("Verified product list contains product with name: {}", expectedName);
    }
}
