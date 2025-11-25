package com.microservices.product.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Product Request DTO (v1)
 * Used for creating and updating products in v1 API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product Request (v1) - Basic product information")
public class ProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    @Schema(description = "Product name", example = "Laptop Pro 15\"")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Product description", example = "High-performance laptop with 16GB RAM")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 digits and 2 decimals")
    @Schema(description = "Product price", example = "1299.99")
    private BigDecimal price;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Schema(description = "Available stock quantity", example = "50")
    private Integer stock;
}
