package com.api.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating an API Object")
public class ApiObjectRequest {
    @NotBlank(message = "Name is required")
    @Schema(description = "Name of the object", example = "iPhone 15 Pro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Schema(description = "Custom data as JSON object", 
            example = "{\"color\": \"blue\", \"price\": 999.99, \"storage\": \"256GB\"}")
    private Object data;
}
