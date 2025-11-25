package com.microservices.product.dto.v2;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product Request DTO (v2)
 * Enhanced version with additional fields for category, SKU, and tags
 * Used for creating and updating products in v2 API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product Request (v2) - Enhanced product information with categorization")
public class ProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    @Schema(description = "Product name", example = "Laptop Pro 15 Gaming Edition")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Product description", example = "High-performance gaming laptop with RTX 4070, 32GB RAM, 1TB SSD")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 digits and 2 decimals")
    @Schema(description = "Product price", example = "1899.99")
    private BigDecimal price;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Schema(description = "Available stock quantity", example = "25")
    private Integer stock;
    
    // v2 Enhanced fields
    @NotBlank(message = "SKU is required in v2")
    @Pattern(regexp = "^[A-Z0-9-]{5,20}$", message = "SKU must be 5-20 characters, uppercase letters, numbers and hyphens only")
    @Schema(description = "Stock Keeping Unit - unique product identifier", example = "LAP-GAME-001")
    private String sku;
    
    @NotBlank(message = "Category is required in v2")
    @Schema(description = "Product category", example = "Electronics", 
            allowableValues = {"Electronics", "Clothing", "Books", "Home", "Sports", "Toys", "Food"})
    private String category;
    
    @Schema(description = "Searchable tags for product", example = "[\"laptop\", \"gaming\", \"portable\", \"high-performance\"]")
    private List<String> tags;
}
