package com.microservices.order.test.steps;

import com.microservices.order.test.context.TestContext;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static io.restassured.RestAssured.given;

@RequiredArgsConstructor
public class OrderV2Steps {

    private final TestContext testContext;

    @When("I create an order v2 with userId {long}, productId {long}, quantity {int}, totalPrice {double}, notes {string}")
    public void iCreateAnOrderV2(Long userId, Long productId, int quantity, double totalPrice, String notes) {
        String requestBody = String.format("""
                {
                    "userId": %d,
                    "productId": %d,
                    "quantity": %d,
                    "totalPrice": %.2f,
                    "shippingAddress": "123 Test Street",
                    "notes": "%s"
                }
                """, userId, productId, quantity, totalPrice, notes);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v2/orders");

        testContext.setResponse(response);
        if (response.getStatusCode() == 201) {
            testContext.setCreatedOrderId(response.jsonPath().getLong("id"));
        }
    }

    @When("I request to get orders by userId {long}")
    public void iRequestToGetOrdersByUserId(Long userId) {
        Response response = given()
                .when()
                .get("/api/v2/orders/user/" + userId);

        testContext.setResponse(response);
    }

    @When("I request to get orders by status {string}")
    public void iRequestToGetOrdersByStatus(String status) {
        Response response = given()
                .when()
                .get("/api/v2/orders/status/" + status);

        testContext.setResponse(response);
    }

    @When("I update order status to {string}")
    public void iUpdateOrderStatusTo(String status) {
        Response response = given()
                .queryParam("status", status)
                .when()
                .patch("/api/v2/orders/" + testContext.getCreatedOrderId() + "/status");

        testContext.setResponse(response);
    }
}
