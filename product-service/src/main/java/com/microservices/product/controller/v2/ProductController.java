package com.microservices.product.controller.v2;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.product.dto.v2.BulkProductRequest;
import com.microservices.product.dto.v2.PagedResponse;
import com.microservices.product.dto.v2.ProductFilterRequest;
import com.microservices.product.dto.v2.ProductRequest;
import com.microservices.product.dto.v2.ProductResponse;
import com.microservices.product.dto.v2.ProductStatsResponse;
import com.microservices.product.service.v2.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Product Controller (v2)
 * Enhanced REST API endpoints with advanced features
 * Base path: /api/v2/products
 * 
 * New features in v2:
 * - Pagination and filtering
 * - Bulk operations
 * - Statistics and analytics
 * - SKU-based lookup
 * - Category management
 * - Soft delete support
 */
@RestController("productControllerV2")
@RequestMapping("/api/v2/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product API v2", description = "Enhanced product management endpoints with pagination, filtering, and analytics")
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * Get all products with pagination and filtering
     */
    @PostMapping("/search")
    @Operation(
        summary = "Search products with filters",
        description = "Advanced search with pagination, filtering by category, price range, tags, and stock status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Filter criteria for product search",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ProductFilterRequest.class),
            examples = {
                @ExampleObject(
                    name = "Basic Search",
                    value = "{\"page\": 0, \"size\": 10, \"sortBy\": \"name\", \"sortDirection\": \"asc\"}"
                ),
                @ExampleObject(
                    name = "Filter by Category",
                    value = "{\"category\": \"Electronics\", \"page\": 0, \"size\": 20}"
                ),
                @ExampleObject(
                    name = "Price Range",
                    value = "{\"minPrice\": 100.00, \"maxPrice\": 1000.00, \"inStock\": true}"
                ),
                @ExampleObject(
                    name = "Tag Search",
                    value = "{\"tag\": \"gaming\", \"sortBy\": \"price\", \"sortDirection\": \"desc\"}"
                )
            }
        )
    )
    public ResponseEntity<PagedResponse<ProductResponse>> searchProducts(
            @Valid @RequestBody ProductFilterRequest filter) {
        log.info("POST /api/v2/products/search - Filtering products");
        PagedResponse<ProductResponse> response = productService.getAllProducts(filter);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve detailed product information including metadata")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/v2/products/{} - Fetching product", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    /**
     * Get product by SKU
     */
    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU", description = "Retrieve product using Stock Keeping Unit identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProductBySku(
            @Parameter(description = "Product SKU", example = "LAP-GAME-001")
            @PathVariable String sku) {
        log.info("GET /api/v2/products/sku/{} - Fetching product by SKU", sku);
        ProductResponse product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }
    
    /**
     * Create new product
     */
    @PostMapping
    @Operation(summary = "Create new product", description = "Create a new product with enhanced validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate SKU")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Product details (IMPORTANT: Change the SKU value to a unique one each time, or use GET /generate-sku endpoint first)",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ProductRequest.class),
            examples = {
                @ExampleObject(
                    name = "Gaming Laptop",
                    value = "{\n" +
                           "  \"name\": \"Gaming Laptop RTX 4070\",\n" +
                           "  \"description\": \"High-performance gaming laptop\",\n" +
                           "  \"price\": 1899.99,\n" +
                           "  \"stock\": 25,\n" +
                           "  \"sku\": \"LAPTOP-RTX-4070\",\n" +
                           "  \"category\": \"Electronics\",\n" +
                           "  \"tags\": [\"laptop\", \"gaming\", \"rtx\"]\n" +
                           "}"
                ),
                @ExampleObject(
                    name = "Wireless Mouse",
                    value = "{\n" +
                           "  \"name\": \"Logitech MX Master 3S\",\n" +
                           "  \"description\": \"Wireless mouse with ergonomic design\",\n" +
                           "  \"price\": 99.99,\n" +
                           "  \"stock\": 50,\n" +
                           "  \"sku\": \"MOUSE-MX-3S\",\n" +
                           "  \"category\": \"Electronics\",\n" +
                           "  \"tags\": [\"mouse\", \"wireless\", \"ergonomic\"]\n" +
                           "}"
                ),
                @ExampleObject(
                    name = "Mechanical Keyboard",
                    value = "{\n" +
                           "  \"name\": \"Keychron K8 Pro\",\n" +
                           "  \"description\": \"Wireless mechanical keyboard\",\n" +
                           "  \"price\": 129.99,\n" +
                           "  \"stock\": 30,\n" +
                           "  \"sku\": \"KEYB-K8-PRO\",\n" +
                           "  \"category\": \"Electronics\",\n" +
                           "  \"tags\": [\"keyboard\", \"mechanical\", \"wireless\"]\n" +
                           "}"
                ),
                @ExampleObject(
                    name = "Programming Book",
                    value = "{\n" +
                           "  \"name\": \"Clean Code\",\n" +
                           "  \"description\": \"A Handbook of Agile Software Craftsmanship\",\n" +
                           "  \"price\": 42.99,\n" +
                           "  \"stock\": 100,\n" +
                           "  \"sku\": \"BOOK-CLEAN-CODE\",\n" +
                           "  \"category\": \"Books\",\n" +
                           "  \"tags\": [\"programming\", \"software\", \"agile\"]\n" +
                           "}"
                ),
                @ExampleObject(
                    name = "Running Shoes",
                    value = "{\n" +
                           "  \"name\": \"Nike Air Zoom Pegasus\",\n" +
                           "  \"description\": \"Professional running shoes\",\n" +
                           "  \"price\": 139.99,\n" +
                           "  \"stock\": 75,\n" +
                           "  \"sku\": \"SHOES-NIKE-PEG\",\n" +
                           "  \"category\": \"Sports\",\n" +
                           "  \"tags\": [\"shoes\", \"running\", \"nike\"]\n" +
                           "}"
                ),
                @ExampleObject(
                    name = "Smart Watch",
                    value = "{\n" +
                           "  \"name\": \"Apple Watch Series 9\",\n" +
                           "  \"description\": \"Advanced health and fitness features\",\n" +
                           "  \"price\": 429.99,\n" +
                           "  \"stock\": 40,\n" +
                           "  \"sku\": \"WATCH-APPLE-S9\",\n" +
                           "  \"category\": \"Electronics\",\n" +
                           "  \"tags\": [\"watch\", \"smart\", \"fitness\"]\n" +
                           "}"
                )
            }
        )
    )
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {
        log.info("POST /api/v2/products - Creating product: {}", request.getName());
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    /**
     * Update product
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product with optimistic locking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate SKU")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        log.info("PUT /api/v2/products/{} - Updating product", id);
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }
    
    /**
     * Delete product (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete product", 
        description = "Soft delete a product (marks as inactive instead of removing from database)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/v2/products/{} - Soft deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Bulk create products
     */
    @PostMapping("/bulk")
    @Operation(
        summary = "Bulk create products", 
        description = "Create multiple products in a single request"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Products created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation errors")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "List of products to create",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BulkProductRequest.class),
            examples = {
                @ExampleObject(
                    name = "Bulk Create Example",
                    value = "{\n" +
                           "  \"products\": [\n" +
                           "    {\n" +
                           "      \"name\": \"Wireless Mouse\",\n" +
                           "      \"description\": \"Ergonomic wireless mouse\",\n" +
                           "      \"price\": 29.99,\n" +
                           "      \"stock\": 50,\n" +
                           "      \"sku\": \"MOUSE-WL-001\",\n" +
                           "      \"category\": \"Electronics\",\n" +
                           "      \"tags\": [\"mouse\", \"wireless\", \"ergonomic\"]\n" +
                           "    },\n" +
                           "    {\n" +
                           "      \"name\": \"Mechanical Keyboard\",\n" +
                           "      \"description\": \"RGB mechanical gaming keyboard\",\n" +
                           "      \"price\": 129.99,\n" +
                           "      \"stock\": 30,\n" +
                           "      \"sku\": \"KEYB-MECH-001\",\n" +
                           "      \"category\": \"Electronics\",\n" +
                           "      \"tags\": [\"keyboard\", \"mechanical\", \"gaming\", \"rgb\"]\n" +
                           "    }\n" +
                           "  ]\n" +
                           "}"
                )
            }
        )
    )
    public ResponseEntity<List<ProductResponse>> bulkCreateProducts(
            @Valid @RequestBody BulkProductRequest request) {
        log.info("POST /api/v2/products/bulk - Creating {} products", request.getProducts().size());
        List<ProductResponse> products = productService.bulkCreateProducts(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(products);
    }
    
    /**
     * Get all categories
     */
    @GetMapping("/categories")
    @Operation(
        summary = "Get all categories", 
        description = "Retrieve list of all product categories"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    public ResponseEntity<List<String>> getAllCategories() {
        log.info("GET /api/v2/products/categories - Fetching all categories");
        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get product statistics
     */
    @GetMapping("/stats")
    @Operation(
        summary = "Get product statistics", 
        description = "Retrieve comprehensive statistics and analytics about products"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public ResponseEntity<ProductStatsResponse> getStatistics() {
        log.info("GET /api/v2/products/stats - Fetching product statistics");
        ProductStatsResponse stats = productService.getStatistics();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Generate random SKU
     */
    @GetMapping("/generate-sku")
    @Operation(
        summary = "Generate random SKU", 
        description = "Generate a unique random SKU for testing purposes"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "SKU generated successfully")
    })
    public ResponseEntity<java.util.Map<String, String>> generateRandomSku() {
        String sku = String.format("PRD-%05d-%04d", 
            System.currentTimeMillis() % 100000,
            (int)(Math.random() * 1000) + 1000); // 1000-1999 for 4 digits
        log.info("GET /api/v2/products/generate-sku - Generated SKU: {}", sku);
        return ResponseEntity.ok(java.util.Map.of("sku", sku));
    }
}
