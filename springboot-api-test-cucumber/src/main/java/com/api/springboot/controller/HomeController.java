package com.api.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Home", description = "Application information and available endpoints")
public class HomeController {
    
    @Operation(summary = "Application home", description = "Returns application information and available API endpoints")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved application info")
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Spring Boot API Testing");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("endpoints", Map.of(
            "GET", "/api/objects - Get all objects",
            "POST", "/api/objects - Create object",
            "GET_BY_ID", "/api/objects/{id} - Get object by ID",
            "PUT", "/api/objects/{id} - Update object",
            "DELETE", "/api/objects/{id} - Delete object",
            "SEARCH", "/api/objects/search?name={name} - Search objects",
            "H2_CONSOLE", "/h2-console - Database console"
        ));
        return ResponseEntity.ok(response);
    }
}
