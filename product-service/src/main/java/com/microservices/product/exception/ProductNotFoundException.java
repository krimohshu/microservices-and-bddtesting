package com.microservices.product.exception;

/**
 * Exception thrown when a product is not found
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
