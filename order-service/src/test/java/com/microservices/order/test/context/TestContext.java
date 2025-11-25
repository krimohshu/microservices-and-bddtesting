package com.microservices.order.test.context;

import io.restassured.response.Response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TestContext {
    private Response response;
    private Long createdOrderId;
    private String apiVersion = "v1";
}
