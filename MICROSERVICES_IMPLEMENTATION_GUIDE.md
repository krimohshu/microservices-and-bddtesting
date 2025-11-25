# Microservices Implementation Guide

## Overview
This document outlines the implementation of User Service and Order Service following the same architectural pattern as Product Service.

## User Service

### Port Configuration
- **Application Port**: 8082
- **Test Port**: Random

### Entity Structure
```java
User {
    - id: Long (Primary Key)
    - username: String (unique, max 50)
    - email: String (unique, max 100)
    - firstName: String (max 50)
    - lastName: String (max 50)
    - phone: String (max 20)
    - role: String (USER, ADMIN, MANAGER) - V2 only
    - status: String (ACTIVE, INACTIVE, SUSPENDED) - V2 only
    - active: Boolean
    - createdAt: LocalDateTime
    - updatedAt: LocalDateTime
    - version: Long (optimistic locking)
}
```

### V1 Endpoints (Basic CRUD)
- GET /api/v1/users - Get all users
- GET /api/v1/users/{id} - Get user by ID
- POST /api/v1/users - Create user
- PUT /api/v1/users/{id} - Update user
- DELETE /api/v1/users/{id} - Delete user
- GET /api/v1/users/search?username={username} - Search by username

### V2 Endpoints (Enhanced)
- POST /api/v2/users/search - Search with pagination & filters
- GET /api/v2/users/{id} - Get user by ID with role/status
- GET /api/v2/users/email/{email} - Get user by email
- POST /api/v2/users - Create user with role/status
- PUT /api/v2/users/{id} - Update user
- DELETE /api/v2/users/{id} - Soft delete
- POST /api/v2/users/bulk - Bulk create users
- GET /api/v2/users/roles - Get all roles
- GET /api/v2/users/stats - Get user statistics
- PUT /api/v2/users/{id}/status - Update user status

### Test Scenarios (Total: ~25 scenarios)

**V1 Tests:**
1. Create user successfully
2. Get user by ID
3. Get all users
4. Update existing user
5. Delete user
6. Search users by username
7. Create user with invalid data (negative)
8. Get non-existent user (negative)
9. Update non-existent user (negative)
10. Delete non-existent user (negative)

**V2 Tests:**
11. Create user with role and status
12. Get user by email
13. Search users with pagination
14. Search users with filters (role, status, name)
15. Bulk create users
16. Update user status
17. Get all roles
18. Get user statistics
19. Soft delete user
20. Create user with duplicate email (negative)
21. Invalid pagination parameters (negative)
22. Search with multiple filters
23. Filter by role
24. Filter by status
25. Bulk create performance test

---

## Order Service

### Port Configuration
- **Application Port**: 8083
- **Test Port**: Random

### Entity Structure
```java
Order {
    - id: Long (Primary Key)
    - orderNumber: String (unique, auto-generated)
    - userId: Long (foreign key reference)
    - productId: Long (foreign key reference)
    - quantity: Integer
    - unitPrice: BigDecimal
    - totalPrice: BigDecimal
    - status: String (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
    - orderDate: LocalDateTime
    - shippingAddress: String - V2 only
    - notes: String - V2 only
    - active: Boolean
    - createdAt: LocalDateTime
    - updatedAt: LocalDateTime
    - version: Long (optimistic locking)
}
```

### V1 Endpoints (Basic CRUD)
- GET /api/v1/orders - Get all orders
- GET /api/v1/orders/{id} - Get order by ID
- POST /api/v1/orders - Create order
- PUT /api/v1/orders/{id} - Update order
- DELETE /api/v1/orders/{id} - Delete order
- GET /api/v1/orders/user/{userId} - Get orders by user

### V2 Endpoints (Enhanced)
- POST /api/v2/orders/search - Search with pagination & filters
- GET /api/v2/orders/{id} - Get order with details
- GET /api/v2/orders/number/{orderNumber} - Get order by number
- POST /api/v2/orders - Create order with validation
- PUT /api/v2/orders/{id} - Update order
- DELETE /api/v2/orders/{id} - Soft delete
- PUT /api/v2/orders/{id}/status - Update order status
- GET /api/v2/orders/user/{userId} - Get user's orders
- GET /api/v2/orders/product/{productId} - Get product's orders
- POST /api/v2/orders/bulk - Bulk create orders
- GET /api/v2/orders/stats - Get order statistics
- GET /api/v2/orders/generate-number - Generate order number

### Test Scenarios (Total: ~30 scenarios)

**V1 Tests:**
1. Create order successfully
2. Get order by ID
3. Get all orders
4. Update existing order
5. Delete order
6. Get orders by user ID
7. Create order with invalid data (negative)
8. Get non-existent order (negative)
9. Update non-existent order (negative)
10. Delete non-existent order (negative)

**V2 Tests:**
11. Create order with shipping address
12. Get order by order number
13. Search orders with pagination
14. Search orders with filters (status, user, product)
15. Update order status
16. Get orders by user ID
17. Get orders by product ID
18. Bulk create orders
19. Get order statistics
20. Generate order number
21. Soft delete order
22. Create order with duplicate number (negative)
23. Invalid pagination (negative)
24. Filter by status
25. Filter by date range
26. Search by user and status
27. Search by product and status
28. Calculate total price correctly
29. Update order with status validation
30. Bulk create performance test

---

## File Structure Pattern

### For Each Service:

```
service-name/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/microservices/{service}/
│   │   │       ├── {Service}Application.java
│   │   │       ├── model/
│   │   │       │   └── {Entity}.java
│   │   │       ├── dto/
│   │   │       │   ├── v1/
│   │   │       │   │   ├── {Entity}Request.java
│   │   │       │   │   └── {Entity}Response.java
│   │   │       │   └── v2/
│   │   │       │       ├── {Entity}Request.java
│   │   │       │       ├── {Entity}Response.java
│   │   │       │       ├── {Entity}FilterRequest.java
│   │   │       │       ├── PagedResponse.java
│   │   │       │       ├── Bulk{Entity}Request.java
│   │   │       │       └── {Entity}StatsResponse.java
│   │   │       ├── repository/
│   │   │       │   └── {Entity}Repository.java
│   │   │       ├── service/
│   │   │       │   ├── v1/
│   │   │       │   │   └── {Entity}Service.java
│   │   │       │   └── v2/
│   │   │       │       └── {Entity}Service.java
│   │   │       ├── controller/
│   │   │       │   ├── v1/
│   │   │       │   │   └── {Entity}Controller.java
│   │   │       │   └── v2/
│   │   │       │       └── {Entity}Controller.java
│   │   │       ├── exception/
│   │   │       │   ├── {Entity}NotFoundException.java
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       └── config/
│   │   │           └── SwaggerConfig.java (optional)
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-test.properties
│   └── test/
│       ├── java/
│       │   └── com/microservices/{service}/test/
│       │       ├── runner/
│       │       │   └── CucumberTestRunner.java
│       │       ├── config/
│       │       │   └── CucumberSpringConfiguration.java
│       │       ├── context/
│       │       │   └── TestContext.java
│       │       ├── hooks/
│       │       │   └── TestHooks.java
│       │       └── steps/
│       │           ├── CommonSteps.java
│       │           ├── {Entity}V1Steps.java
│       │           └── {Entity}V2Steps.java
│       └── resources/
│           ├── features/
│           │   ├── {entity}-v1.feature
│           │   └── {entity}-v2.feature
│           ├── junit-platform.properties
│           └── application-test.properties
```

---

## Implementation Steps

### 1. User Service (Estimated: 25 files)
- [x] pom.xml
- [ ] Application class
- [ ] User entity
- [ ] V1 DTOs (2 files)
- [ ] V2 DTOs (6 files)
- [ ] Repository with custom queries
- [ ] V1 Service
- [ ] V2 Service
- [ ] V1 Controller
- [ ] V2 Controller
- [ ] Exception classes (2 files)
- [ ] Test infrastructure (4 files)
- [ ] Test step definitions (3 files)
- [ ] Feature files (2 files)
- [ ] Configuration files (4 files)

### 2. Order Service (Estimated: 25 files)
- Same structure as User Service

---

## Key Differences Between Services

### Product Service
- Focus: Product catalog management
- Unique Fields: SKU, category, tags, stock
- Special Features: Stock status calculation, category grouping

### User Service
- Focus: User management
- Unique Fields: username, email, role, status
- Special Features: Role-based operations, status management

### Order Service
- Focus: Order processing
- Unique Fields: orderNumber, userId, productId, status
- Special Features: Price calculations, status workflow, user/product relationships

---

## Next Steps

1. **Complete User Service Implementation** (~2-3 hours)
   - Create all entity, DTO, repository, service, controller files
   - Implement test infrastructure
   - Write and execute all test scenarios

2. **Complete Order Service Implementation** (~2-3 hours)
   - Same as User Service

3. **Integration Testing** (~1 hour)
   - Test all three services together
   - Verify port configurations
   - Run complete test suite

4. **Documentation** (~30 minutes)
   - Update README files
   - Document API endpoints
   - Create Postman collections (optional)

---

## Commands to Build and Test

### User Service
```bash
cd user-service
mvn clean install
mvn spring-boot:run  # Runs on port 8082
mvn test  # Runs Cucumber tests
```

### Order Service
```bash
cd order-service
mvn clean install
mvn spring-boot:run  # Runs on port 8083
mvn test  # Runs Cucumber tests
```

### Run All Services
```bash
# Terminal 1
cd product-service && mvn spring-boot:run

# Terminal 2
cd user-service && mvn spring-boot:run

# Terminal 3
cd order-service && mvn spring-boot:run
```

### Access Swagger UI
- Product Service: http://localhost:8081/swagger-ui.html
- User Service: http://localhost:8082/swagger-ui.html
- Order Service: http://localhost:8083/swagger-ui.html
