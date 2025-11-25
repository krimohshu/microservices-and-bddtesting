package com.microservices.product.service.v1;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.product.dto.v1.ProductRequest;
import com.microservices.product.dto.v1.ProductResponse;
import com.microservices.product.exception.ProductNotFoundException;
import com.microservices.product.model.Product;
import com.microservices.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Product Service (v1)
 * Business logic for product operations
 */
@Service("productServiceV1")
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository repository;
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.debug("Fetching all products (v1)");
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.debug("Fetching product by id: {} (v1)", id);
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return toResponse(product);
    }
    
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.debug("Creating new product: {} (v1)", request.getName());
        Product product = toEntity(request);
        Product saved = repository.save(product);
        return toResponse(saved);
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.debug("Updating product id: {} (v1)", id);
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        
        Product updated = repository.save(product);
        return toResponse(updated);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Deleting product id: {} (v1)", id);
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String name) {
        log.debug("Searching products by name: {} (v1)", name);
        return repository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    // Helper methods
    private Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        return product;
    }
    
    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}
