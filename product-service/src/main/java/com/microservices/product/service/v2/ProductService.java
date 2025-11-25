package com.microservices.product.service.v2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.product.dto.v2.BulkProductRequest;
import com.microservices.product.dto.v2.PagedResponse;
import com.microservices.product.dto.v2.ProductFilterRequest;
import com.microservices.product.dto.v2.ProductRequest;
import com.microservices.product.dto.v2.ProductResponse;
import com.microservices.product.dto.v2.ProductStatsResponse;
import com.microservices.product.exception.ProductNotFoundException;
import com.microservices.product.model.Product;
import com.microservices.product.repository.ProductRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Product Service (v2)
 * Enhanced business logic with advanced features:
 * - Pagination and filtering
 * - Bulk operations
 * - Statistics and analytics
 * - Stock status calculation
 * - Soft delete support
 */
@Service("productServiceV2")
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    
    /**
     * Get all products with pagination and filtering
     */
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(ProductFilterRequest filter) {
        log.info("Fetching products with filter: {}", filter);
        
        // Build specification for dynamic filtering
        Specification<Product> spec = buildSpecification(filter);
        
        // Create pageable with sorting
        Sort sort = Sort.by(
            filter.getSortDirection().equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            filter.getSortBy()
        );
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        
        // Execute query
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        
        // Convert to response
        List<ProductResponse> responses = productPage.getContent().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return PagedResponse.<ProductResponse>builder()
            .content(responses)
            .pageNumber(productPage.getNumber())
            .pageSize(productPage.getSize())
            .totalElements(productPage.getTotalElements())
            .totalPages(productPage.getTotalPages())
            .first(productPage.isFirst())
            .last(productPage.isLast())
            .hasNext(productPage.hasNext())
            .hasPrevious(productPage.hasPrevious())
            .build();
    }
    
    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product by id: {}", id);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return convertToResponse(product);
    }
    
    /**
     * Get product by SKU
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        log.info("Fetching product by SKU: {}", sku);
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        return convertToResponse(product);
    }
    
    /**
     * Create new product
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product: {}", request.getName());
        
        // Check if SKU already exists
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Product with SKU " + request.getSku() + " already exists");
        }
        
        Product product = convertToEntity(request);
        Product saved = productRepository.save(product);
        log.info("Product created with id: {}", saved.getId());
        
        return convertToResponse(saved);
    }
    
    /**
     * Update product
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product id: {}", id);
        
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        
        // Check if SKU is being changed and if new SKU already exists
        if (!existing.getSku().equals(request.getSku()) && 
            productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Product with SKU " + request.getSku() + " already exists");
        }
        
        // Update fields
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        existing.setSku(request.getSku());
        existing.setCategory(request.getCategory());
        existing.setTags(convertTagsToString(request.getTags()));
        
        Product updated = productRepository.save(existing);
        log.info("Product updated: {}", updated.getId());
        
        return convertToResponse(updated);
    }
    
    /**
     * Delete product (soft delete)
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Soft deleting product id: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        
        product.setActive(false);
        productRepository.save(product);
        
        log.info("Product soft deleted: {}", id);
    }
    
    /**
     * Bulk create products
     */
    @Transactional
    public List<ProductResponse> bulkCreateProducts(BulkProductRequest request) {
        log.info("Bulk creating {} products", request.getProducts().size());
        
        List<Product> products = request.getProducts().stream()
            .map(this::convertToEntity)
            .collect(Collectors.toList());
        
        List<Product> saved = productRepository.saveAll(products);
        log.info("Bulk created {} products", saved.size());
        
        return saved.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all categories
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        log.info("Fetching all categories");
        List<Product> products = productRepository.findByActive(true);
        return products.stream()
            .map(Product::getCategory)
            .filter(Objects::nonNull)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * Get product statistics
     */
    @Transactional(readOnly = true)
    public ProductStatsResponse getStatistics() {
        log.info("Calculating product statistics");
        
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByActive(true);
        long inactiveProducts = productRepository.countByActive(false);
        
        // Category counts - filter out null categories
        List<Object[]> categoryData = productRepository.countByCategory();
        Map<String, Long> categoryMap = categoryData.stream()
            .filter(row -> row[0] != null)
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));
        
        BigDecimal avgPrice = productRepository.findAveragePrice();
        BigDecimal maxPrice = productRepository.findMaxPrice();
        BigDecimal minPrice = productRepository.findMinPrice();
        Long totalStock = productRepository.findTotalStock();
        Long outOfStock = productRepository.countOutOfStock();
        Long lowStock = productRepository.countLowStock();
        Long totalCategories = productRepository.countDistinctCategories();
        
        return ProductStatsResponse.builder()
            .totalProducts(totalProducts)
            .activeProducts(activeProducts)
            .inactiveProducts(inactiveProducts)
            .totalCategories(totalCategories)
            .productsByCategory(categoryMap)
            .averagePrice(avgPrice != null ? avgPrice : BigDecimal.ZERO)
            .maxPrice(maxPrice != null ? maxPrice : BigDecimal.ZERO)
            .minPrice(minPrice != null ? minPrice : BigDecimal.ZERO)
            .totalStock(totalStock != null ? totalStock : 0L)
            .outOfStockCount(outOfStock)
            .lowStockCount(lowStock)
            .build();
    }
    
    // Helper methods
    
    /**
     * Build JPA Specification for dynamic filtering
     */
    private Specification<Product> buildSpecification(ProductFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Active filter (default: show only active)
            if (filter.getActiveOnly() != null && filter.getActiveOnly()) {
                predicates.add(criteriaBuilder.equal(root.get("active"), true));
            }
            
            // Name filter
            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + filter.getName().toLowerCase() + "%"
                ));
            }
            
            // Category filter
            if (filter.getCategory() != null && !filter.getCategory().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("category"), filter.getCategory()));
            }
            
            // Price range filter
            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }
            
            // Tag filter
            if (filter.getTag() != null && !filter.getTag().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("tags"),
                    "%" + filter.getTag().toLowerCase() + "%"
                ));
            }
            
            // Stock filter
            if (filter.getInStock() != null) {
                if (filter.getInStock()) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("stock"), 0));
                } else {
                    predicates.add(criteriaBuilder.equal(root.get("stock"), 0));
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Convert Product entity to v2 Response DTO
     */
    private ProductResponse convertToResponse(Product product) {
        return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .stock(product.getStock())
            .sku(product.getSku())
            .category(product.getCategory())
            .tags(convertTagsToList(product.getTags()))
            .active(product.getActive())
            .version(product.getVersion())
            .stockStatus(calculateStockStatus(product.getStock()))
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
    }
    
    /**
     * Convert v2 Request DTO to Product entity
     */
    private Product convertToEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSku(request.getSku());
        product.setCategory(request.getCategory());
        product.setTags(convertTagsToString(request.getTags()));
        product.setActive(true);
        return product;
    }
    
    /**
     * Calculate stock status based on quantity
     */
    private String calculateStockStatus(Integer stock) {
        if (stock == null || stock == 0) {
            return "OUT_OF_STOCK";
        } else if (stock < 10) {
            return "LOW_STOCK";
        } else {
            return "IN_STOCK";
        }
    }
    
    /**
     * Convert tags list to comma-separated string
     */
    private String convertTagsToString(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return String.join(",", tags);
    }
    
    /**
     * Convert comma-separated string to tags list
     */
    private List<String> convertTagsToList(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(tags.split(","));
    }
}
