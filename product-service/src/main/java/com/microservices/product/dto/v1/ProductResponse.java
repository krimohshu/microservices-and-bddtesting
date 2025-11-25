package com.microservices.product.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product Response DTO (v1)
 * Used for returning product data in v1 API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product Response (v1) - Product information")
public class ProductResponse {
    
    @Schema(description = "Product ID", example = "1")
    private Long id;
    
    @Schema(description = "Product name", example = "Laptop Pro 15\"")
    private String name;
    
    @Schema(description = "Product description", example = "High-performance laptop with 16GB RAM")
    private String description;
    
    @Schema(description = "Product price", example = "1299.99")
    private BigDecimal price;
    
    @Schema(description = "Available stock", example = "50")
    private Integer stock;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
