package com.microservices.user.test.context;

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
    private final ThreadLocal<Long> createdUserId = new ThreadLocal<>();
    private final ThreadLocal<String> requestBody = new ThreadLocal<>();
    
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
     * Store created user ID
     */
    public void setCreatedUserId(Long userId) {
        this.createdUserId.set(userId);
    }
    
    /**
     * Get created user ID
     */
    public Long getCreatedUserId() {
        return this.createdUserId.get();
    }
    
    /**
     * Store request body
     */
    public void setRequestBody(String body) {
        this.requestBody.set(body);
    }
    
    /**
     * Get request body
     */
    public String getRequestBody() {
        return this.requestBody.get();
    }
    
    /**
     * Store test data by key
     */
    public void setTestData(String key, Object value) {
        this.testData.get().put(key, value);
    }
    
    /**
     * Retrieve test data by key
     */
    @SuppressWarnings("unchecked")
    public <T> T getTestData(String key) {
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
    public void reset() {
        this.response.remove();
        this.createdUserId.remove();
        this.requestBody.remove();
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
