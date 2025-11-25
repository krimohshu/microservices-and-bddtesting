package com.microservices.order.test.hooks;

import com.microservices.order.repository.OrderRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.server.LocalServerPort;

@RequiredArgsConstructor
public class TestHooks {

    @LocalServerPort
    private int port;

    private final OrderRepository orderRepository;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @After
    public void tearDown() {
        orderRepository.deleteAll();
    }
}
