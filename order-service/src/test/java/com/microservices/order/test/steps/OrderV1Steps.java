package com.microservices.order.test.steps;

import com.microservices.order.test.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static io.restassured.RestAssured.given;

@RequiredArgsConstructor
public class OrderV1Steps {

    private final TestContext testContext;

    @When("I create an order with userId {long}, productId {long}, quantity {int}, totalPrice {double}")
    public void iCreateAnOrder(Long userId, Long productId, int quantity, double totalPrice) {
        String requestBody = String.format("""
                {
                    "userId": %d,
                    "productId": %d,
                    "quantity": %d,
                    "totalPrice": %.2f,
                    "shippingAddress": "123 Test Street"
                }
                """, userId, productId, quantity, totalPrice);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/" + testContext.getApiVersion() + "/orders");

        testContext.setResponse(response);
        if (response.getStatusCode() == 201) {
            testContext.setCreatedOrderId(response.jsonPath().getLong("id"));
        }
    }

    @When("I request to get order by ID")
    public void iRequestToGetOrderById() {
        Response response = given()
                .when()
                .get("/api/" + testContext.getApiVersion() + "/orders/" + testContext.getCreatedOrderId());

        testContext.setResponse(response);
    }

    @When("I request to get all orders")
    public void iRequestToGetAllOrders() {
        Response response = given()
                .when()
                .get("/api/" + testContext.getApiVersion() + "/orders");

        testContext.setResponse(response);
    }

    @When("I update the order with quantity {int}")
    public void iUpdateTheOrderWithQuantity(int quantity) {
        String requestBody = String.format("""
                {
                    "userId": 1,
                    "productId": 1,
                    "quantity": %d,
                    "totalPrice": 100.00,
                    "shippingAddress": "456 Updated Street"
                }
                """, quantity);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/" + testContext.getApiVersion() + "/orders/" + testContext.getCreatedOrderId());

        testContext.setResponse(response);
    }

    @When("I delete the order")
    public void iDeleteTheOrder() {
        Response response = given()
                .when()
                .delete("/api/" + testContext.getApiVersion() + "/orders/" + testContext.getCreatedOrderId());

        testContext.setResponse(response);
    }
}
