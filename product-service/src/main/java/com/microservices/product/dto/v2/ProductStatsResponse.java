package com.microservices.product.dto.v2;

import java.math.BigDecimal;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product Statistics Response DTO (v2)
 * Provides statistical information about products
 * Used for analytics and reporting
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product Statistics - Analytics and aggregate data")
public class ProductStatsResponse {
    
    @Schema(description = "Total number of products", example = "150")
    private Long totalProducts;
    
    @Schema(description = "Number of active products", example = "145")
    private Long activeProducts;
    
    @Schema(description = "Number of inactive (deleted) products", example = "5")
    private Long inactiveProducts;
    
    @Schema(description = "Total number of categories", example = "7")
    private Long totalCategories;
    
    @Schema(description = "Product count by category", 
            example = "{\"Electronics\": 50, \"Clothing\": 40, \"Books\": 30}")
    private Map<String, Long> productsByCategory;
    
    @Schema(description = "Average product price", example = "599.99")
    private BigDecimal averagePrice;
    
    @Schema(description = "Highest product price", example = "2999.99")
    private BigDecimal maxPrice;
    
    @Schema(description = "Lowest product price", example = "9.99")
    private BigDecimal minPrice;
    
    @Schema(description = "Total stock across all products", example = "5000")
    private Long totalStock;
    
    @Schema(description = "Number of products out of stock", example = "12")
    private Long outOfStockCount;
    
    @Schema(description = "Number of products with low stock (<10 units)", example = "25")
    private Long lowStockCount;
}
