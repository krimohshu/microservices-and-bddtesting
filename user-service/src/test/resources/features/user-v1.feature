Feature: User Management V1 API
  As a user of the User Service
  I want to manage users using the V1 API
  So that I can perform basic CRUD operations

  Scenario: Create a new user successfully
    Given I have a valid user request
    When I create a new user
    Then the response status code should be 201
    And the response should contain field "id"
    And the response should contain field "username"
    And the response should contain field "email"

  Scenario: Get user by ID
    Given I have a valid user request
    When I create a new user
    And I get user by ID
    Then the response status code should be 200
    And the response should contain field "id"
    And the response should contain field "firstName"
    And the response should contain field "lastName"

  Scenario: Get all users
    Given I have a valid user request
    When I create a new user
    And I get all users
    Then the response status code should be 200
    And the response should be a list
    And the response list should not be empty

  Scenario: Update an existing user
    Given I have a valid user request
    When I create a new user
    And I update the user
    Then the response status code should be 200
    And the response should contain field "id"
    And the response field "firstName" should be "Updated"

  Scenario: Delete a user
    Given I have a valid user request
    When I create a new user
    And I delete the user
    Then the response status code should be 204

  Scenario: Search users by username
    Given I have a valid user request
    When I create a new user
    And I search users by username "test"
    Then the response status code should be 200
    And the response should be a list

  Scenario: Create user with missing username
    Given I have an invalid user request with missing "username"
    When I create a new user
    Then the response status code should be 400
    And the response should contain error message

  Scenario: Create user with missing email
    Given I have an invalid user request with missing "email"
    When I create a new user
    Then the response status code should be 400
    And the response should contain error message

  Scenario: Get non-existent user
    When I get user by invalid ID 99999
    Then the response status code should be 404
    And the response should contain error message

  Scenario: Update non-existent user
    When I update user with invalid ID 99999
    Then the response status code should be 404
    And the response should contain error message

  Scenario: Delete non-existent user
    When I delete user with invalid ID 99999
    Then the response status code should be 404
    And the response should contain error message
