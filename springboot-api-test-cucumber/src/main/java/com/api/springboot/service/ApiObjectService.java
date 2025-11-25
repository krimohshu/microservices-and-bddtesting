package com.api.springboot.service;

import com.api.springboot.dto.ApiObjectRequest;
import com.api.springboot.dto.ApiObjectResponse;
import com.api.springboot.exception.ResourceNotFoundException;
import com.api.springboot.model.ApiObject;
import com.api.springboot.repository.ApiObjectRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiObjectService {
    
    private final ApiObjectRepository repository;
    private final ObjectMapper objectMapper;
    
    public List<ApiObjectResponse> getAllObjects() {
        return repository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public ApiObjectResponse getObjectById(Long id) {
        ApiObject object = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found with id: " + id));
        return convertToResponse(object);
    }
    
    @Transactional
    public ApiObjectResponse createObject(ApiObjectRequest request) {
        ApiObject object = new ApiObject();
        object.setName(request.getName());
        object.setData(convertDataToJson(request.getData()));
        
        ApiObject saved = repository.save(object);
        return convertToResponse(saved);
    }
    
    @Transactional
    public ApiObjectResponse updateObject(Long id, ApiObjectRequest request) {
        ApiObject object = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found with id: " + id));
        
        object.setName(request.getName());
        object.setData(convertDataToJson(request.getData()));
        
        ApiObject updated = repository.save(object);
        return convertToResponse(updated);
    }
    
    @Transactional
    public void deleteObject(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Object not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    public List<ApiObjectResponse> searchByName(String name) {
        return repository.findByNameContaining(name).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private ApiObjectResponse convertToResponse(ApiObject object) {
        ApiObjectResponse response = new ApiObjectResponse();
        response.setId(object.getId());
        response.setName(object.getName());
        response.setData(convertJsonToObject(object.getData()));
        response.setCreatedAt(object.getCreatedAt());
        response.setUpdatedAt(object.getUpdatedAt());
        return response;
    }
    
    private String convertDataToJson(Object data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting data to JSON", e);
        }
    }
    
    private Object convertJsonToObject(String json) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            return json;
        }
    }
}
