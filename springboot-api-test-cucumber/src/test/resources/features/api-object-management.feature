Feature: API Object Management
  As a REST API client
  I want to perform CRUD operations on API objects
  So that I can manage data effectively

  Background:
    Given the API is running

  Scenario: Create a new object
    When I create an object with name "Test Object" and data:
      """
      {
        "color": "blue",
        "price": 99.99
      }
      """
    Then the response status code should be 201
    And the response should contain a valid object id
    And the object should have name "Test Object"

  Scenario: Get all objects
    Given the following objects exist:
      | name          | data                            |
      | Object 1      | {"color": "red", "price": 10}   |
      | Object 2      | {"color": "green", "price": 20} |
    When I request all objects
    Then the response status code should be 200
    And the response should contain at least 2 objects

  Scenario: Get object by ID
    Given an object with name "Test Object" exists
    When I request the object by its ID
    Then the response status code should be 200
    And the object should have name "Test Object"

  Scenario: Update an object
    Given an object with name "Original Name" exists
    When I update the object with name "Updated Name"
    Then the response status code should be 200
    And the object should have name "Updated Name"

  Scenario: Delete an object
    Given an object with name "To Delete" exists
    When I delete the object
    Then the response status code should be 204
    And the object should not exist anymore

  Scenario: Get non-existent object
    When I request an object with id 99999
    Then the response status code should be 404
    And the response should contain error message "Object not found with id: 99999"

  Scenario: Create object with invalid data
    When I create an object with empty name
    Then the response status code should be 400
    And the response should contain validation error

  Scenario: Search objects by name
    Given the following objects exist:
      | name          | data                            |
      | Apple Phone   | {"color": "silver", "price": 999} |
      | Apple Laptop  | {"color": "space gray", "price": 1999} |
      | Samsung Phone | {"color": "black", "price": 799}  |
    When I search for objects with name "Apple"
    Then the response status code should be 200
    And the response should contain 2 objects

  Scenario: Verify data persistence
    When I create an object with name "Persistent Object" and data:
      """
      {
        "category": "electronics",
        "available": true
      }
      """
    And I retrieve the created object
    Then the data should match the original data

  Scenario: Concurrent object creation
    When I create 5 objects concurrently
    Then all objects should be created successfully
    And each object should have a unique ID

  Scenario: Update non-existent object
    When I try to update object with id 99999
    Then the response status code should be 404

  Scenario: Delete non-existent object
    When I try to delete object with id 99999
    Then the response status code should be 404

  Scenario: Verify timestamps
    When I create an object with name "Timestamp Test"
    Then the object should have createdAt timestamp
    And the object should have updatedAt timestamp
    When I update the object with name "Timestamp Updated"
    Then the updatedAt timestamp should be different from createdAt

  Scenario: Create multiple objects and verify count
    Given the API has no objects
    When I create 3 objects
    And I request all objects
    Then the response should contain exactly 3 objects

  Scenario: Verify object data types
    When I create an object with name "Data Types Test" and data:
      """
      {
        "string": "text",
        "number": 42,
        "boolean": true,
        "array": [1, 2, 3],
        "nested": {"key": "value"}
      }
      """
    Then the object data should preserve all data types
