@v1 @order
Feature: Order Management API v1
  As a client of the order service
  I want to manage orders using REST API v1
  So that I can perform CRUD operations on orders

  Background:
    Given the API version is "v1"

  @smoke @create
  Scenario: Create a new order
    When I create an order with userId 1, productId 100, quantity 2, totalPrice 199.99
    Then the response status code should be 201
    And the response should contain field "id"
    And the response field "status" should be "PENDING"

  @retrieve
  Scenario: Get order by ID
    When I create an order with userId 1, productId 100, quantity 2, totalPrice 199.99
    And I request to get order by ID
    Then the response status code should be 200
    And the response should contain field "id"
    And the response should contain field "userId"

  @list
  Scenario: Get all orders
    When I create an order with userId 1, productId 100, quantity 2, totalPrice 199.99
    And I request to get all orders
    Then the response status code should be 200

  @update
  Scenario: Update an existing order
    When I create an order with userId 1, productId 100, quantity 2, totalPrice 199.99
    And I update the order with quantity 5
    Then the response status code should be 200
    And the response should contain field "id"

  @delete
  Scenario: Delete an order
    When I create an order with userId 1, productId 100, quantity 2, totalPrice 199.99
    And I delete the order
    Then the response status code should be 204
