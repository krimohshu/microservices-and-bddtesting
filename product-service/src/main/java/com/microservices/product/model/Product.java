package com.microservices.product.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product Entity (Enhanced for v1 and v2)
 * Supports both v1 (basic) and v2 (enhanced) API operations
 * - v1 uses: id, name, description, price, stock, createdAt, updatedAt
 * - v2 adds: category, sku, tags, active, version for advanced features
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_sku", columnList = "sku"),
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_active", columnList = "active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock;
    
    // v2 Enhanced fields
    @Column(unique = true, length = 50)
    private String sku; // Stock Keeping Unit - unique product identifier
    
    @Column(length = 50)
    private String category; // Product category (Electronics, Clothing, Books, etc.)
    
    @Column(length = 500)
    private String tags; // Comma-separated tags for search (e.g., "laptop,gaming,portable")
    
    @Column(nullable = false)
    private Boolean active = true; // Soft delete flag - false means deleted
    
    @Version // Optimistic locking for concurrent updates
    private Long version;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (active == null) {
            active = true; // Default to active for backward compatibility
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
