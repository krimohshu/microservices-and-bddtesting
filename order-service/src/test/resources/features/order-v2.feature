@v2 @order
Feature: Order Management API v2
  As a client of the order service
  I want to manage orders using REST API v2
  So that I can perform enhanced CRUD operations on orders with additional features

  Background:
    Given the API version is "v2"

  @smoke @create
  Scenario: Create a new order with notes
    When I create an order v2 with userId 1, productId 100, quantity 2, totalPrice 199.99, notes "Express delivery"
    Then the response status code should be 201
    And the response should contain field "id"
    And the response field "status" should be "PENDING"
    And the response should contain field "notes"

  @filter
  Scenario: Get orders by user ID
    When I create an order v2 with userId 1, productId 100, quantity 2, totalPrice 199.99, notes "User order"
    And I request to get orders by userId 1
    Then the response status code should be 200

  @filter
  Scenario: Get orders by status
    When I create an order v2 with userId 1, productId 100, quantity 2, totalPrice 199.99, notes "Pending order"
    And I request to get orders by status "PENDING"
    Then the response status code should be 200

  @status
  Scenario: Update order status
    When I create an order v2 with userId 1, productId 100, quantity 2, totalPrice 199.99, notes "Status update test"
    And I update order status to "CONFIRMED"
    Then the response status code should be 200
    And the response field "status" should be "CONFIRMED"
