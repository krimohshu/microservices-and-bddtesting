package com.api.springboot.test.context;

import io.restassured.response.Response;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
public class TestContext {
    private Response response;
    private Long currentObjectId;
    private Map<String, Object> currentObjectData;
    private List<Long> createdObjectIds = new ArrayList<>();
    private Map<String, Object> testData = new ConcurrentHashMap<>();
    
    public void reset() {
        response = null;
        currentObjectId = null;
        currentObjectData = null;
        testData.clear();
    }
    
    public void addCreatedObjectId(Long id) {
        createdObjectIds.add(id);
    }
    
    public void storeData(String key, Object value) {
        testData.put(key, value);
    }
    
    public Object getData(String key) {
        return testData.get(key);
    }
}
