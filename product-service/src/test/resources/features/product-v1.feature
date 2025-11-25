@ProductV1 @API
Feature: Product Service V1 API
  As a QA Engineer
  I want to test the Product Service V1 REST API
  So that I can ensure all CRUD operations work correctly

  Background:
    Given the Product Service V1 is running

  @Smoke @Create
  Scenario: Create a new product successfully
    When I send a POST request to "/api/v1/products" with body:
      """
      {
        "name": "Gaming Laptop",
        "description": "High-performance gaming laptop with RTX 4080",
        "price": 1899.99,
        "stock": 25
      }
      """
    Then the response status should be 201
    And the response should contain:
      | field       | value                                            |
      | name        | Gaming Laptop                                     |
      | description | High-performance gaming laptop with RTX 4080      |
      | price       | 1899.99                                          |
      | stock       | 25                                               |
    And the response should have field "id"
    And the response should have field "createdAt"

  @Smoke @Read
  Scenario: Get a product by ID
    Given I create a product with name "Wireless Mouse" and price 29.99
    When I send a GET request to "/api/v1/products/{productId}"
    Then the response status should be 200
    And the response should contain:
      | field | value          |
      | name  | Wireless Mouse |
      | price | 29.99          |

  @Read
  Scenario: Get all products
    Given I create a product with name "Keyboard" and price 79.99
    And I create a product with name "Monitor" and price 299.99
    When I send a GET request to "/api/v1/products"
    Then the response status should be 200
    And the response should be a list
    And the response list should have at least 2 items

  @Update
  Scenario: Update an existing product
    Given I create a product with name "USB Cable" and price 9.99
    When I send a PUT request to "/api/v1/products/{productId}" with body:
      """
      {
        "name": "Premium USB-C Cable",
        "description": "Durable 2m USB-C cable",
        "price": 14.99,
        "stock": 100
      }
      """
    Then the response status should be 200
    And the response should contain:
      | field       | value                    |
      | name        | Premium USB-C Cable      |
      | description | Durable 2m USB-C cable   |
      | price       | 14.99                    |
      | stock       | 100                      |

  @Delete
  Scenario: Delete a product
    Given I create a product with name "Test Product" and price 19.99
    When I send a DELETE request to "/api/v1/products/{productId}"
    Then the response status should be 204
    And when I send a GET request to "/api/v1/products/{productId}"
    Then the response status should be 404

  @Search
  Scenario: Search products by name
    Given I create a product with name "Gaming Mouse RGB" and price 49.99
    And I create a product with name "Office Mouse" and price 19.99
    When I send a GET request to "/api/v1/products/search?name=Gaming"
    Then the response status should be 200
    And the response should be a list
    And the response list should contain product with name "Gaming Mouse RGB"

  @Validation @Negative
  Scenario: Create product with invalid data
    When I send a POST request to "/api/v1/products" with body:
      """
      {
        "name": "",
        "price": -10,
        "stock": -5
      }
      """
    Then the response status should be 400

  @Negative
  Scenario: Get non-existent product
    When I send a GET request to "/api/v1/products/99999"
    Then the response status should be 404

  @Negative
  Scenario: Update non-existent product
    When I send a PUT request to "/api/v1/products/99999" with body:
      """
      {
        "name": "Test",
        "price": 10.0,
        "stock": 5
      }
      """
    Then the response status should be 404

  @Negative
  Scenario: Delete non-existent product
    When I send a DELETE request to "/api/v1/products/99999"
    Then the response status should be 404
