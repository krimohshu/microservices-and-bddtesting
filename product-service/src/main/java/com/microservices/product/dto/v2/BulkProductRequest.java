package com.microservices.product.dto.v2;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bulk Product Request DTO (v2)
 * Used for bulk create/update operations
 * Allows creating or updating multiple products in a single request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Bulk Product Request - Create or update multiple products")
public class BulkProductRequest {
    
    @NotEmpty(message = "Products list cannot be empty")
    @Valid
    @Schema(description = "List of products to create or update")
    private List<ProductRequest> products;
}
