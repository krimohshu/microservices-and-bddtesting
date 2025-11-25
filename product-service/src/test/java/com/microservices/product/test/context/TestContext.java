package com.microservices.product.test.context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;

/**
 * Test Context for storing test data across step definitions
 * Thread-safe implementation for parallel test execution
 */
@Component
@Getter
@Setter
public class TestContext {
    
    // Thread-safe storage for test data
    private final ThreadLocal<Response> response = new ThreadLocal<>();
    private final ThreadLocal<Map<String, Object>> testData = ThreadLocal.withInitial(ConcurrentHashMap::new);
    
    /**
     * Store the last HTTP response
     */
    public void setResponse(Response response) {
        this.response.set(response);
    }
    
    /**
     * Get the last HTTP response
     */
    public Response getResponse() {
        return this.response.get();
    }
    
    /**
     * Store test data by key
     */
    public void setData(String key, Object value) {
        this.testData.get().put(key, value);
    }
    
    /**
     * Retrieve test data by key
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) this.testData.get().get(key);
    }
    
    /**
     * Check if data exists for key
     */
    public boolean hasData(String key) {
        return this.testData.get().containsKey(key);
    }
    
    /**
     * Clear all test data for current thread
     */
    public void clear() {
        this.response.remove();
        this.testData.get().clear();
        this.testData.remove();
    }
    
    /**
     * Get all test data for current thread
     */
    public Map<String, Object> getAllData() {
        return new HashMap<>(this.testData.get());
    }
}
