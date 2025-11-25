package com.microservices.product.repository;

import com.microservices.product.model.ProductV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product Repository (v2)
 * Enhanced JPA Repository with category and rating queries
 */
@Repository
public interface ProductV2Repository extends JpaRepository<ProductV2, Long> {
    
    List<ProductV2> findByNameContainingIgnoreCase(String name);
    
    List<ProductV2> findByCategory(String category);
    
    List<ProductV2> findByRatingGreaterThanEqual(BigDecimal minRating);
    
    @Query("SELECT p FROM ProductV2 p WHERE p.stock > 0")
    List<ProductV2> findAvailableProducts();
    
    @Query("SELECT p FROM ProductV2 p WHERE p.category = :category AND p.rating >= :minRating")
    List<ProductV2> findByCategoryAndMinRating(String category, BigDecimal minRating);
}
