package com.microservices.product.dto.v2;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product Filter Request DTO (v2)
 * Used for filtering and searching products with multiple criteria
 * Supports pagination, sorting, and multi-field filtering
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product Filter Request - Advanced search criteria")
public class ProductFilterRequest {
    
    @Schema(description = "Search by product name (partial match)", example = "laptop")
    private String name;
    
    @Schema(description = "Filter by category", example = "Electronics")
    private String category;
    
    @Schema(description = "Minimum price", example = "100.00")
    private BigDecimal minPrice;
    
    @Schema(description = "Maximum price", example = "2000.00")
    private BigDecimal maxPrice;
    
    @Schema(description = "Search by tag", example = "gaming")
    private String tag;
    
    @Schema(description = "Filter by stock availability", example = "true")
    private Boolean inStock; // true = stock > 0, false = stock = 0
    
    @Builder.Default
    @Schema(description = "Filter active products only", example = "true", defaultValue = "true")
    private Boolean activeOnly = true; // Default: show only active products
    
    // Pagination
    @Builder.Default
    @Min(value = 0, message = "Page number must be 0 or greater")
    @Schema(description = "Page number (0-based)", example = "0", defaultValue = "0")
    private Integer page = 0;
    
    @Builder.Default
    @Min(value = 1, message = "Page size must be at least 1")
    @Schema(description = "Page size", example = "10", defaultValue = "10")
    private Integer size = 10;
    
    @Builder.Default
    @Schema(description = "Sort field", example = "name", 
            allowableValues = {"name", "price", "stock", "category", "createdAt", "updatedAt"})
    private String sortBy = "name";
    
    @Builder.Default
    @Schema(description = "Sort direction", example = "asc", allowableValues = {"asc", "desc"})
    private String sortDirection = "asc";
}
