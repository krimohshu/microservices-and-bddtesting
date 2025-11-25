package com.api.springboot.controller;

import com.api.springboot.dto.ApiObjectRequest;
import com.api.springboot.dto.ApiObjectResponse;
import com.api.springboot.service.ApiObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/objects")
@RequiredArgsConstructor
@Tag(name = "API Objects", description = "CRUD operations for API Objects")
public class ApiObjectController {
    
    private final ApiObjectService service;
    
    @Operation(summary = "Get all objects", description = "Retrieves all API objects from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
    })
    @GetMapping
    public ResponseEntity<List<ApiObjectResponse>> getAllObjects() {
        return ResponseEntity.ok(service.getAllObjects());
    }
    
    @Operation(summary = "Get object by ID", description = "Retrieves a specific API object by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Object found"),
        @ApiResponse(responseCode = "404", description = "Object not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiObjectResponse> getObjectById(
            @Parameter(description = "ID of the object to retrieve", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(service.getObjectById(id));
    }
    
    @Operation(
        summary = "Create a new object", 
        description = "Creates a new API object with custom data",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "API Object to create",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiObjectRequest.class),
                examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "iPhone Example",
                        summary = "Create an iPhone object",
                        value = "{\"name\": \"iPhone 15 Pro\", \"data\": {\"color\": \"blue\", \"price\": 999.99, \"storage\": \"256GB\"}}"
                    ),
                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Laptop Example",
                        summary = "Create a laptop object",
                        value = "{\"name\": \"MacBook Pro\", \"data\": {\"color\": \"space gray\", \"price\": 2499.99, \"ram\": \"32GB\", \"screen\": \"16 inch\"}}"
                    ),
                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Book Example",
                        summary = "Create a book object",
                        value = "{\"name\": \"Clean Code\", \"data\": {\"author\": \"Robert Martin\", \"price\": 45.99, \"pages\": 464, \"isbn\": \"978-0132350884\"}}"
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Object created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<ApiObjectResponse> createObject(@Valid @RequestBody ApiObjectRequest request) {
        ApiObjectResponse created = service.createObject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @Operation(
        summary = "Update an object", 
        description = "Updates an existing API object",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated object data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiObjectRequest.class),
                examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Update iPhone",
                        summary = "Update iPhone details",
                        value = "{\"name\": \"iPhone 15 Pro Max\", \"data\": {\"color\": \"titanium\", \"price\": 1199.99, \"storage\": \"512GB\"}}"
                    ),
                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Update Laptop",
                        summary = "Update laptop details",
                        value = "{\"name\": \"MacBook Pro M3\", \"data\": {\"color\": \"silver\", \"price\": 2999.99, \"ram\": \"64GB\", \"screen\": \"16 inch\"}}"
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Object updated successfully"),
        @ApiResponse(responseCode = "404", description = "Object not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiObjectResponse> updateObject(
            @Parameter(description = "ID of the object to update", example = "1") @PathVariable Long id,
            @Valid @RequestBody ApiObjectRequest request) {
        return ResponseEntity.ok(service.updateObject(id, request));
    }
    
    @Operation(summary = "Delete an object", description = "Deletes an API object by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Object deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Object not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObject(
            @Parameter(description = "ID of the object to delete", example = "1") @PathVariable Long id) {
        service.deleteObject(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Search objects by name", description = "Searches for API objects containing the specified name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ApiObjectResponse>> searchByName(
            @Parameter(description = "Name to search for", example = "iPhone") @RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }
}
