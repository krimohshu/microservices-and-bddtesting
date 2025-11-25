package com.microservices.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Product Service Application
 * 
 * Microservice for managing product catalog with API versioning (v1 and v2)
 * - v1: Basic product operations (Production)
 * - v2: Enhanced with categories and ratings (QA)
 * 
 * Port: 8081
 * Swagger UI: http://localhost:8081/swagger-ui/index.html
 * H2 Console: http://localhost:8081/h2-console
 */
@SpringBootApplication
public class ProductServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
