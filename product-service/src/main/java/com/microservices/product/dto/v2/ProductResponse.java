package com.microservices.product.dto.v2;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product Response DTO (v2)
 * Enhanced response with category, SKU, tags, and metadata
 * Used for returning product data in v2 API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product Response (v2) - Enhanced product information with metadata")
public class ProductResponse {
    
    @Schema(description = "Product ID", example = "1")
    private Long id;
    
    @Schema(description = "Product name", example = "Laptop Pro 15 Gaming Edition")
    private String name;
    
    @Schema(description = "Product description", example = "High-performance gaming laptop")
    private String description;
    
    @Schema(description = "Product price", example = "1899.99")
    private BigDecimal price;
    
    @Schema(description = "Available stock", example = "25")
    private Integer stock;
    
    // v2 Enhanced fields
    @Schema(description = "Stock Keeping Unit", example = "LAP-GAME-001")
    private String sku;
    
    @Schema(description = "Product category", example = "Electronics")
    private String category;
    
    @Schema(description = "Product tags", example = "[\"laptop\", \"gaming\", \"portable\"]")
    private List<String> tags;
    
    @Schema(description = "Active status (false = soft deleted)", example = "true")
    private Boolean active;
    
    @Schema(description = "Version for optimistic locking", example = "1")
    private Long version;
    
    // Metadata
    @Schema(description = "Stock status based on quantity")
    private String stockStatus; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
