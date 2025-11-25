package com.microservices.product.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.microservices.product.model.Product;

/**
 * Product Repository (Enhanced for v1 and v2)
 * JPA Repository with advanced query support
 * - v1: Basic queries (findByNameContaining, findAvailableProducts, etc.)
 * - v2: Advanced queries (pagination, filtering, specifications, statistics)
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    // v1 Basic queries
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findAvailableProducts();
    
    @Query("SELECT p FROM Product p WHERE p.stock < :threshold")
    List<Product> findLowStockProducts(Integer threshold);
    
    // v2 Enhanced queries
    
    // Find by SKU (unique identifier)
    Optional<Product> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    // Find by category with pagination
    Page<Product> findByCategory(String category, Pageable pageable);
    
    // Find active products
    Page<Product> findByActive(Boolean active, Pageable pageable);
    
    List<Product> findByActive(Boolean active);
    
    // Find by category and active status
    Page<Product> findByCategoryAndActive(String category, Boolean active, Pageable pageable);
    
    // Find by name containing (case-insensitive) with pagination
    Page<Product> findByNameContainingIgnoreCaseAndActive(String name, Boolean active, Pageable pageable);
    
    // Find by price range
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.active = :active")
    Page<Product> findByPriceRange(
        @Param("minPrice") BigDecimal minPrice, 
        @Param("maxPrice") BigDecimal maxPrice, 
        @Param("active") Boolean active,
        Pageable pageable
    );
    
    // Find by tag (tags stored as comma-separated string)
    @Query("SELECT p FROM Product p WHERE p.tags LIKE %:tag% AND p.active = :active")
    Page<Product> findByTag(@Param("tag") String tag, @Param("active") Boolean active, Pageable pageable);
    
    // Find in stock products with pagination
    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.active = :active")
    Page<Product> findInStockProducts(@Param("active") Boolean active, Pageable pageable);
    
    // Statistics queries for v2
    
    // Count by category
    @Query("SELECT p.category, COUNT(p) FROM Product p WHERE p.active = true GROUP BY p.category")
    List<Object[]> countByCategory();
    
    // Count active products
    long countByActive(Boolean active);
    
    // Average price
    @Query("SELECT AVG(p.price) FROM Product p WHERE p.active = true")
    BigDecimal findAveragePrice();
    
    // Max price
    @Query("SELECT MAX(p.price) FROM Product p WHERE p.active = true")
    BigDecimal findMaxPrice();
    
    // Min price
    @Query("SELECT MIN(p.price) FROM Product p WHERE p.active = true")
    BigDecimal findMinPrice();
    
    // Total stock
    @Query("SELECT SUM(p.stock) FROM Product p WHERE p.active = true")
    Long findTotalStock();
    
    // Out of stock count
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock = 0 AND p.active = true")
    Long countOutOfStock();
    
    // Low stock count
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock > 0 AND p.stock < 10 AND p.active = true")
    Long countLowStock();
    
    // Count distinct categories
    @Query("SELECT COUNT(DISTINCT p.category) FROM Product p WHERE p.active = true")
    Long countDistinctCategories();
}
