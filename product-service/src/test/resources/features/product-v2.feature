@ProductV2 @API
Feature: Product Service V2 API - Enhanced Features
  As a QA Engineer
  I want to test the Product Service V2 REST API with enhanced features
  So that I can ensure pagination, filtering, bulk operations, and statistics work correctly

  Background:
    Given the Product Service V2 is running

  @Smoke @Create
  Scenario: Create a product with enhanced V2 fields
    Given I generate a random SKU
    When I send a POST request to "/api/v2/products" with body:
      """
      {
        "name": "Gaming Laptop Pro",
        "description": "Professional gaming laptop with RTX 4090",
        "price": 2499.99,
        "stock": 15,
        "sku": "{generatedSku}",
        "category": "Electronics",
        "tags": ["gaming", "laptop", "high-performance"],
        "active": true
      }
      """
    Then the response status should be 201
    And the response should contain:
      | field       | value                                        |
      | name        | Gaming Laptop Pro                             |
      | category    | Electronics                                   |
      | active      | true                                          |
      | stockStatus | IN_STOCK                                      |
    And the response field "tags" should contain "gaming"

  @Read @SKU
  Scenario: Get product by SKU
    Given I create a V2 product with SKU "PRD-TEST-001" and category "Books"
    When I send a GET request to "/api/v2/products/sku/PRD-TEST-001"
    Then the response status should be 200
    And the response should contain:
      | field    | value        |
      | sku      | PRD-TEST-001 |
      | category | Books        |

  @Search @Pagination
  Scenario: Search products with pagination
    Given I create 5 products in category "Electronics"
    When I send a POST request to "/api/v2/products/search" with body:
      """
      {
        "category": "Electronics",
        "page": 0,
        "size": 3,
        "sortBy": "price",
        "sortDirection": "ASC"
      }
      """
    Then the response status should be 200
    And the response should have pagination info
    And the pagination "totalElements" should be at least 5
    And the pagination "size" should be 3
    And the pagination "number" should be 0

  @Search @Filter
  Scenario: Search products with multiple filters
    Given I create a V2 product with name "Budget Laptop" price 599.99 category "Electronics"
    And I create a V2 product with name "Premium Laptop" price 1999.99 category "Electronics"
    When I send a POST request to "/api/v2/products/search" with body:
      """
      {
        "category": "Electronics",
        "minPrice": 500.0,
        "maxPrice": 1000.0,
        "active": true
      }
      """
    Then the response status should be 200
    And the response content should contain product with name "Budget Laptop"
    And the response content should not contain product with name "Premium Laptop"

  @Search @Tags
  Scenario: Search products by tags
    Given I create a V2 product with name "Wireless Gaming Mouse" and category "Electronics"
    When I send a POST request to "/api/v2/products/search" with body:
      """
      {
        "tag": "test",
        "category": "Electronics"
      }
      """
    Then the response status should be 200
    And the response should not be empty

  @Bulk @Create
  Scenario: Bulk create products
    Given I have a list of 3 products to create in bulk
    When I send a POST request to "/api/v2/products/bulk" with the bulk product list
    Then the response status should be 201
    And the response should be a list
    And the response list should have 3 items

  @Update @SoftDelete
  Scenario: Soft delete a product (set active to false)
    Given I create a V2 product with name "Temp Product" and category "Test"
    When I send a PUT request to "/api/v2/products/{productId}" with body:
      """
      {
        "name": "Temp Product",
        "description": "Temporary test product",
        "price": 10.0,
        "stock": 5,
        "sku": "TEMP-SKU-001",
        "category": "Test",
        "tags": ["test"],
        "active": false
      }
      """
    Then the response status should be 200
    And the response should contain:
      | field  | value |
      | active | false |

  @Categories
  Scenario: Get all product categories
    Given I create a V2 product with category "Electronics"
    And I create a V2 product with category "Books"
    And I create a V2 product with category "Clothing"
    When I send a GET request to "/api/v2/products/categories"
    Then the response status should be 200
    And the response should be a list
    And the response list should contain "Electronics"
    And the response list should contain "Books"
    And the response list should contain "Clothing"

  @Statistics
  Scenario: Get product statistics
    Given I create 3 products with different prices in category "Electronics"
    When I send a GET request to "/api/v2/products/stats"
    Then the response status should be 200
    And the response should have field "totalProducts"
    And the response should have field "totalCategories"
    And the response should have field "averagePrice"
    And the response should have field "maxPrice"
    And the response should have field "totalStock"

  @Utility @SKU
  Scenario: Generate a random SKU
    When I send a GET request to "/api/v2/products/generate-sku"
    Then the response status should be 200
    And the response should have field "sku"
    And the response field "sku" should match pattern "PRD-\d+-\d+"

  @Search @StockStatus
  Scenario: Filter products by stock status
    Given I create a V2 product with stock 0
    And I create a V2 product with stock 15
    When I send a POST request to "/api/v2/products/search" with body:
      """
      {
        "inStock": true
      }
      """
    Then the response status should be 200
    And the response content should contain product with name "Stock Test Product"

  @Validation @Negative
  Scenario: Create product with duplicate SKU
    Given I create a V2 product with SKU "DUPLICATE-SKU-001" and category "Test"
    When I send a POST request to "/api/v2/products" with body:
      """
      {
        "name": "Duplicate Product",
        "description": "Test duplicate SKU",
        "price": 99.99,
        "stock": 10,
        "sku": "DUPLICATE-SKU-001",
        "category": "Test",
        "tags": ["test"]
      }
      """
    Then the response status should be 400

  @Negative
  Scenario: Get product with invalid SKU
    When I send a GET request to "/api/v2/products/sku/INVALID-SKU-999"
    Then the response status should be 404

  @Negative @Pagination
  Scenario: Search with invalid pagination parameters
    When I send a POST request to "/api/v2/products/search" with body:
      """
      {
        "page": -1,
        "size": 0
      }
      """
    Then the response status should be 400

  @Performance @Bulk
  Scenario: Bulk create large number of products
    Given I have a list of 50 products to create in bulk
    When I send a POST request to "/api/v2/products/bulk" with the bulk product list
    Then the response status should be 201
    And the response time should be less than 5000 ms
    And the response list should have 50 items
