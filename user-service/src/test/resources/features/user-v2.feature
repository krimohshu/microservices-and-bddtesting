Feature: User Management V2 API
  As a user of the User Service
  I want to use advanced user management features
  So that I can perform enhanced operations with pagination, filtering, and bulk operations

  Scenario: Create user with role and status
    Given I have a valid V2 user request with role "ADMIN" and status "ACTIVE"
    When I create a new V2 user
    Then the response status code should be 201
    And the response should contain field "id"
    And the V2 response field "role" should be "ADMIN"
    And the V2 response field "status" should be "ACTIVE"

  Scenario: Search users with pagination
    Given I have a valid V2 user request with role "USER" and status "ACTIVE"
    When I create a new V2 user
    And I search V2 users with pagination page 0 size 10
    Then the response status code should be 200
    And the V2 response should have pagination info

  Scenario: Search users with role filter
    Given I have a valid V2 user request with role "MANAGER" and status "ACTIVE"
    When I create a new V2 user
    And I search V2 users with filter role "MANAGER"
    Then the response status code should be 200
    And the V2 response should have pagination info

  Scenario: Search users with status filter
    Given I have a valid V2 user request with role "USER" and status "INACTIVE"
    When I create a new V2 user
    And I search V2 users with filter status "INACTIVE"
    Then the response status code should be 200
    And the V2 response should have pagination info

  Scenario: Get user by email
    Given I have a valid V2 user request with role "USER" and status "ACTIVE"
    When I create a new V2 user
    And I get V2 user by email
    Then the response status code should be 200
    And the response should contain field "email"
    And the V2 response field "role" should be "USER"

  Scenario: Bulk create users
    When I bulk create 5 users
    Then the response status code should be 201
    And the response should be a list
    And the response list should have 5 items

  Scenario: Update user status
    Given I have a valid V2 user request with role "USER" and status "ACTIVE"
    When I create a new V2 user
    And I update V2 user status to "SUSPENDED"
    Then the response status code should be 200
    And the V2 response field "status" should be "SUSPENDED"

  Scenario: Get all roles
    When I get all V2 roles
    Then the response status code should be 200
    And the response should be a list

  Scenario: Get user statistics
    Given I have a valid V2 user request with role "USER" and status "ACTIVE"
    When I create a new V2 user
    And I get V2 user statistics
    Then the response status code should be 200
    And the response should contain field "totalUsers"
    And the response should contain field "activeUsers"
    And the response should contain field "usersByRole"
    And the response should contain field "usersByStatus"

  Scenario: Generate unique username
    When I generate username with firstName "John" and lastName "Doe"
    Then the response status code should be 200
    And the response should contain field "username"

  Scenario: Soft delete user
    Given I have a valid V2 user request with role "USER" and status "ACTIVE"
    When I create a new V2 user
    And I soft delete V2 user
    Then the response status code should be 204

  Scenario: Create user with duplicate email
    Given I have a valid V2 user request with role "USER" and status "ACTIVE"
    When I create a new V2 user
    And I have a duplicate email user request
    And I create a new V2 user
    Then the response status code should be 400
    And the response should contain error message

  Scenario: Search with invalid pagination (negative page)
    When I search V2 users with invalid page -1
    Then the response status code should be 400
    And the response should contain error message

  Scenario: Search users with multiple filters
    Given I have a valid V2 user request with role "ADMIN" and status "ACTIVE"
    When I create a new V2 user
    And I search V2 users with filter role "ADMIN"
    Then the response status code should be 200
    And the V2 response should have pagination info

  Scenario: Bulk create performance test
    When I bulk create 10 users
    Then the response status code should be 201
    And the response list should have 10 items
