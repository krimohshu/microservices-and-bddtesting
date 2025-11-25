# Spring Boot API Testing Framework

A comprehensive REST API testing framework built with **Spring Boot 3.2.0**, **Swagger/OpenAPI**, **Cucumber BDD**, and **REST Assured** featuring thread-safe parallel test execution and interactive API documentation.

## 🚀 Features

### Core Features
- ✅ **Spring Boot 3.2.0** REST API with complete CRUD operations
- ✅ **Swagger/OpenAPI 3.0** with interactive UI and example payloads
- ✅ **In-Memory H2 Database** for fast, isolated testing with web console
- ✅ **JPA/Hibernate 6.3.1** for data persistence with automatic schema generation
- ✅ **Bean Validation** for request validation with proper error messages
- ✅ **Global Exception Handling** with standardized error responses
- ✅ **Root Endpoint** with application info and available endpoints
- ✅ **Lombok** for reduced boilerplate code

### Testing Features
- ✅ **Cucumber BDD** with 15 comprehensive test scenarios (Gherkin syntax)
- ✅ **REST Assured 5.3.2** for API testing with fluent assertions
- ✅ **Thread-Safe Parallel Execution** (~60% performance improvement)
- ✅ **JUnit 5** platform with dynamic test strategy
- ✅ **Isolated Test Environment** with clean database per test

### Documentation & Tooling
- ✅ **Interactive Swagger UI** - Try APIs directly in browser
- ✅ **Multiple Request Examples** - Pre-filled payloads for testing
- ✅ **H2 Console** - Visual database browser at `/h2-console`
- ✅ **Comprehensive API Documentation** - Auto-generated from annotations

## 📋 Prerequisites

- **Java 17** (required - configured for this project)
- **Maven 3.9+** or higher
- **Git** (for cloning the repository)

## 🏗️ Project Structure

```
springboot-api-testing/
├── src/
│   ├── main/
│   │   ├── java/com/api/springboot/
│   │   │   ├── Application.java           # Spring Boot main class
│   │   │   ├── config/
│   │   │   │   └── ApplicationConfig.java # Application configuration
│   │   │   ├── controller/
│   │   │   │   └── ApiObjectController.java # REST endpoints
│   │   │   ├── dto/
│   │   │   │   ├── ApiObjectRequest.java   # Request DTO
│   │   │   │   └── ApiObjectResponse.java  # Response DTO
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   └── ApiObject.java         # JPA Entity
│   │   │   ├── repository/
│   │   │   │   └── ApiObjectRepository.java # JPA Repository
│   │   │   └── service/
│   │   │       └── ApiObjectService.java   # Business logic
│   │   └── resources/
│   │       └── application.properties      # App configuration
│   └── test/
│       ├── java/com/api/springboot/test/
│       │   ├── config/
│       │   │   └── CucumberSpringConfiguration.java
│       │   ├── context/
│       │   │   └── TestContext.java        # Test state management
│       │   ├── hooks/
│       │   │   └── TestHooks.java          # Before/After hooks
│       │   ├── runner/
│       │   │   └── TestRunner.java         # JUnit Platform Suite
│       │   └── steps/
│       │       ├── GivenSteps.java         # Setup steps
│       │       ├── WhenSteps.java          # Action steps
│       │       └── ThenSteps.java          # Assertion steps
│       └── resources/
│           ├── application-test.properties
│           ├── junit-platform.properties   # Parallel execution config
│           └── features/
│               └── api-object-management.feature
└── pom.xml
```

## 🔧 API Endpoints

### Application Endpoints
| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/` | Application home & info | 200 OK |
| GET | `/swagger-ui/index.html` | Interactive API documentation | 200 OK |
| GET | `/api-docs` | OpenAPI JSON specification | 200 OK |
| GET | `/h2-console` | H2 Database web console | 200 OK |

### REST API Endpoints
| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/objects` | Get all objects | - | 200 OK + Array |
| GET | `/api/objects/{id}` | Get object by ID | - | 200 OK / 404 |
| POST | `/api/objects` | Create new object | JSON | 201 Created |
| PUT | `/api/objects/{id}` | Update object | JSON | 200 OK / 404 |
| DELETE | `/api/objects/{id}` | Delete object | - | 204 No Content / 404 |
| GET | `/api/objects/search?name={name}` | Search by name | - | 200 OK + Array |

## 📦 API Usage Examples

### Using Swagger UI (Recommended)
1. Open `http://localhost:9091/swagger-ui/index.html`
2. Click on any endpoint to expand
3. Click **"Try it out"**
4. Select an example from dropdown or modify payload
5. Click **"Execute"**
6. View response below

### Using cURL Commands

#### 1. Create Object (POST)
```bash
curl -X POST http://localhost:9091/api/objects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "data": {
      "color": "blue",
      "price": 999.99,
      "storage": "256GB"
    }
  }'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "data": {
    "color": "blue",
    "price": 999.99,
    "storage": "256GB"
  },
  "createdAt": "2025-11-25T12:00:00.123456",
  "updatedAt": "2025-11-25T12:00:00.123456"
}
```

#### 2. Get All Objects (GET)
```bash
curl http://localhost:9091/api/objects
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "iPhone 15 Pro",
    "data": {
      "color": "blue",
      "price": 999.99,
      "storage": "256GB"
    },
    "createdAt": "2025-11-25T12:00:00.123456",
    "updatedAt": "2025-11-25T12:00:00.123456"
  }
]
```

#### 3. Get Object by ID (GET)
```bash
curl http://localhost:9091/api/objects/1
```

#### 4. Update Object (PUT)
```bash
curl -X PUT http://localhost:9091/api/objects/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro Max",
    "data": {
      "color": "titanium",
      "price": 1199.99,
      "storage": "512GB"
    }
  }'
```

#### 5. Search Objects (GET)
```bash
curl "http://localhost:9091/api/objects/search?name=iPhone"
```

#### 6. Delete Object (DELETE)
```bash
curl -X DELETE http://localhost:9091/api/objects/1
```

**Response: 204 No Content**

### Error Responses

#### 404 Not Found
```json
{
  "status": 404,
  "message": "Object not found with id: 99999",
  "timestamp": "2025-11-25T12:00:00.123456"
}
```

#### 400 Bad Request (Validation Error)
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": [
    "name: Name is required"
  ],
  "timestamp": "2025-11-25T12:00:00.123456"
}
```

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/krimohshu/springboot-api-test-cucumber.git
cd springboot-api-test-cucumber
```

### 2. Install Java 17 (if not already installed)

**macOS (using Homebrew):**
```bash
brew install openjdk@17
# Add to shell profile
echo 'export JAVA_HOME=/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

**Verify Java version:**
```bash
java -version  # Should show 17.x.x
mvn -version   # Should show Java version 17
```

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The application will start on **`http://localhost:9091`**

## 🧪 Testing the Application

### Run All Tests (Cucumber BDD with Parallel Execution)
```bash
mvn clean test
```

**Test Performance:**
- Sequential: ~8-10 seconds
- Parallel (4 threads): ~3-4 seconds
- **~60% performance improvement**

### View Test Reports
After running tests:
```bash
open target/cucumber-reports/cucumber.html
```

## 🌐 Accessing the Application

Once the application is running, access these URLs:

### 1. **Root Endpoint** (Application Info)
```
http://localhost:9091/
```
Returns application status and available endpoints.

### 2. **Swagger UI** (Interactive API Documentation)
```
http://localhost:9091/swagger-ui/index.html
```
- Try all APIs directly in the browser
- Pre-filled example payloads
- View request/response schemas
- Test authentication (if added)

### 3. **H2 Database Console**
```
URL: http://localhost:9091/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leave empty)
```

**SQL Queries to Try:**
```sql
-- View all objects
SELECT * FROM API_OBJECTS;

-- Count objects
SELECT COUNT(*) FROM API_OBJECTS;

-- Search by name
SELECT * FROM API_OBJECTS WHERE NAME LIKE '%iPhone%';
```

## 🔄 Parallel Execution

Tests are configured to run in parallel using Cucumber's dynamic strategy:

**Configuration** (`junit-platform.properties`):
```properties
cucumber.execution.parallel.enabled=true
cucumber.execution.parallel.config.strategy=dynamic
cucumber.execution.parallel.config.dynamic.factor=1.0
```

**Performance:**
- Sequential: ~8-10 seconds
- Parallel (4 threads): ~3-4 seconds
- **~60% performance improvement**

## 🧪 Test Scenarios

The framework includes **15 comprehensive BDD scenarios** covering:

### Happy Path Scenarios
1. ✅ **Create a new object** - POST with valid data
2. ✅ **Get all objects** - Retrieve list of all stored objects
3. ✅ **Get object by ID** - Retrieve specific object
4. ✅ **Update an object** - PUT with modified data
5. ✅ **Delete an object** - Remove object from database
6. ✅ **Search objects by name** - Query with search parameter

### Error Handling Scenarios
7. ✅ **Get non-existent object** - Returns 404 with error message
8. ✅ **Create object with invalid data** - Validation error (400)
9. ✅ **Update non-existent object** - Returns 404
10. ✅ **Delete non-existent object** - Returns 404

### Data Integrity Scenarios
11. ✅ **Verify data persistence** - Data survives request cycle
12. ✅ **Verify timestamps** - createdAt & updatedAt auto-generated
13. ✅ **Verify object data types** - JSON data types preserved
14. ✅ **Create multiple objects and verify count** - Batch operations

### Concurrency Scenarios
15. ✅ **Concurrent object creation** - Thread-safe parallel execution

### Test Structure

```
src/test/
├── java/com/api/springboot/test/
│   ├── config/
│   │   └── CucumberSpringConfiguration.java    # Spring test context
│   ├── context/
│   │   └── TestContext.java                     # Thread-safe state management
│   ├── hooks/
│   │   └── TestHooks.java                       # Before/After test setup
│   ├── runner/
│   │   └── TestRunner.java                      # JUnit Platform Suite
│   └── steps/
│       ├── GivenSteps.java                      # Setup test data
│       ├── WhenSteps.java                       # API calls & actions
│       └── ThenSteps.java                       # Assertions & verifications
└── resources/
    ├── application-test.properties              # Test configuration
    ├── junit-platform.properties                # Parallel execution config
    └── features/
        └── api-object-management.feature        # Gherkin scenarios

```

### Sample Test Scenario (Gherkin)

```gherkin
Feature: API Object Management
  As a REST API client
  I want to perform CRUD operations on API objects
  So that I can manage data effectively

  Scenario: Create a new object
    Given the API is running
    When I create an object with name "iPhone 15 Pro" and data:
      """
      {
        "color": "blue",
        "price": 999.99,
        "storage": "256GB"
      }
      """
    Then the response status code should be 201
    And the response should contain a valid object id
    And the object should have name "iPhone 15 Pro"
```

### Running Specific Test Scenarios

```bash
# Run all tests
mvn test

# Run with verbose output
mvn test -Dcucumber.plugin="pretty"

# Run specific feature file
mvn test -Dcucumber.features=src/test/resources/features/api-object-management.feature

# Run scenarios with specific tag (if tags are added)
mvn test -Dcucumber.filter.tags="@smoke"
```

## 🛡️ Thread-Safety

The framework is designed with thread-safety in mind:

- **REST Assured `path()` method**: Uses native Java JSON parsing (thread-safe)
- **Spring Boot Test Context**: Proper isolation per test
- **TestContext Component**: Thread-safe data storage using ConcurrentHashMap
- **Database Isolation**: Each test uses clean H2 in-memory database

## 🏗️ Architecture

### Layered Architecture
```
┌─────────────────────────────────────────────────────────┐
│                    Client Layer                          │
│  (Browser, Postman, Swagger UI, Test Client)            │
└─────────────────────┬───────────────────────────────────┘
                      │ HTTP Requests
┌─────────────────────▼───────────────────────────────────┐
│              Controller Layer                            │
│  ┌────────────────┐  ┌──────────────────────┐          │
│  │ HomeController │  │ ApiObjectController  │          │
│  │   GET /        │  │   /api/objects/*     │          │
│  └────────────────┘  └──────────────────────┘          │
│              ▲  DTO (Request/Response)  ▲               │
└──────────────┼─────────────────────────┼───────────────┘
               │                          │
┌──────────────▼──────────────────────────▼───────────────┐
│              Service Layer                               │
│  ┌─────────────────────────────────────────┐            │
│  │      ApiObjectService                    │            │
│  │   (Business Logic & Validation)          │            │
│  └─────────────────────────────────────────┘            │
│                       │                                  │
└───────────────────────┼──────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────┐
│           Repository Layer (Spring Data JPA)             │
│  ┌─────────────────────────────────────────┐            │
│  │      ApiObjectRepository                 │            │
│  │   (JPA CRUD Operations)                  │            │
│  └─────────────────────────────────────────┘            │
│                       │                                  │
└───────────────────────┼──────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────┐
│                 Data Layer                               │
│  ┌─────────────────────────────────────────┐            │
│  │    H2 In-Memory Database                 │            │
│  │    (jdbc:h2:mem:testdb)                  │            │
│  └─────────────────────────────────────────┘            │
└──────────────────────────────────────────────────────────┘
```

### Key Design Patterns

#### 1. **Layered Architecture**
- Clear separation of concerns
- Each layer has specific responsibility
- Dependencies flow downward only

#### 2. **Repository Pattern**
- JPA repositories for data access
- Abstraction over data persistence
- Query methods defined by convention

#### 3. **DTO Pattern**
- Separate request/response objects from entities
- `ApiObjectRequest` for input validation
- `ApiObjectResponse` for controlled output

#### 4. **Service Layer Pattern**
- Business logic centralized in services
- Transaction management
- Coordinates between controller and repository

#### 5. **Dependency Injection**
- Constructor injection with Lombok `@RequiredArgsConstructor`
- Promotes testability and loose coupling
- Spring manages object lifecycle

#### 6. **Exception Handling**
- Centralized with `@RestControllerAdvice`
- Global exception handler for consistent error responses
- Custom exceptions (e.g., `ResourceNotFoundException`)

#### 7. **Bean Validation**
- Declarative validation with annotations
- `@NotBlank`, `@Valid`, etc.
- Automatic validation before controller methods

### Component Interaction Flow

```
1. Client sends HTTP request
   ↓
2. Controller receives request, validates DTO
   ↓
3. Controller calls Service method
   ↓
4. Service executes business logic
   ↓
5. Service calls Repository
   ↓
6. Repository interacts with Database (JPA/Hibernate)
   ↓
7. Database returns Entity
   ↓
8. Service converts Entity to Response DTO
   ↓
9. Controller returns HTTP response
```

### Documentation & Testing Architecture

```
┌──────────────────────────────────────────────────────┐
│             Swagger/OpenAPI Layer                    │
│  • Auto-generated API documentation                  │
│  • Interactive testing interface                     │
│  • Request/Response schemas                          │
└────────────────────┬─────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────┐
│            Cucumber BDD Testing                      │
│  ┌──────────────┐  ┌─────────────┐  ┌────────────┐ │
│  │Feature Files │→ │Step         │→ │REST        │ │
│  │(Gherkin)     │  │Definitions  │  │Assured     │ │
│  └──────────────┘  └─────────────┘  └────────────┘ │
│         │                 │                │         │
│         └─────────────────┴────────────────┘         │
│                           ↓                          │
│                    Spring Boot App                   │
└──────────────────────────────────────────────────────┘
```

For detailed architecture diagrams, see [ARCHITECTURE.md](ARCHITECTURE.md)

## 📊 Test Reports

After running tests, view the Cucumber report:
```bash
open target/cucumber-reports/cucumber.html
```

## 🔍 Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.2.0 | Web framework & dependency injection |
| Java | 17 | Programming language (LTS version) |
| Spring Data JPA | 3.2.0 | Data persistence abstraction |
| Hibernate | 6.3.1 | ORM framework |
| H2 Database | 2.2.224 | In-memory database |
| SpringDoc OpenAPI | 2.2.0 | Swagger/OpenAPI documentation |
| REST Assured | 5.3.2 | API testing library |
| Cucumber | 7.14.0 | BDD framework (Gherkin) |
| JUnit 5 | 5.10.0 | Test runner & assertions |
| Maven | 3.9+ | Build automation & dependency management |
| Lombok | 1.18.30 | Code generation (reduces boilerplate) |
| Jackson | 2.14.2 | JSON serialization/deserialization |
| Bean Validation | 3.0 | Request validation annotations |

## ⚙️ Configuration

### Application Configuration (`application.properties`)

```properties
# Server Configuration
server.port=9091

# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Swagger/OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha

# Logging
logging.level.com.api.springboot=DEBUG
logging.level.org.springframework.web=INFO
```

### Test Configuration (`junit-platform.properties`)

```properties
# Parallel Execution
cucumber.execution.parallel.enabled=true
cucumber.execution.parallel.config.strategy=dynamic
cucumber.execution.parallel.config.dynamic.factor=1.0
```

### VS Code Java Configuration (`.vscode/settings.json`)

```json
{
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-17",
            "path": "/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home",
            "default": true
        }
    ],
    "java.jdt.ls.java.home": "/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home",
    "maven.terminal.customEnv": [
        {
            "environmentVariable": "JAVA_HOME",
            "value": "/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
        }
    ]
}
```

## 🚀 Quick Start

1. **Clone the repository**
```bash
git clone <repository-url>
cd springboot-api-testing
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run tests**
```bash
mvn test
```

4. **Run the application**
```bash
mvn spring-boot:run
```

## 📝 Development Guide

### Adding New Endpoints

1. **Create DTO classes** (if needed)
2. **Add repository method** (if custom query needed)
3. **Implement service method** with business logic
4. **Create controller endpoint** with proper annotations
5. **Add Swagger documentation** with examples
6. **Write Cucumber test scenarios**
7. **Implement step definitions**

### Writing New Tests

1. **Add scenario** to `api-object-management.feature`
```gherkin
Scenario: Custom test scenario
  Given the API is running
  When I perform some action
  Then I should see expected result
```

2. **Implement step definitions** in appropriate file:
```java
@When("I perform some action")
public void performAction() {
    // Implementation using REST Assured
    Response response = given()
        .when()
        .get("/api/objects");
    
    // Store in TestContext if needed
    testContext.setResponse(response);
}
```

3. **Add assertions** in `ThenSteps.java`:
```java
@Then("I should see expected result")
public void verifyResult() {
    Response response = testContext.getResponse();
    response.then()
        .statusCode(200)
        .body("size()", greaterThan(0));
}
```

4. **Run tests**: `mvn test`

### Adding Swagger Examples

Add example payloads to controller methods:

```java
@Operation(
    summary = "Create object",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
            examples = @ExampleObject(
                name = "Example Name",
                value = "{\"name\": \"Test\", \"data\": {}}"
            )
        )
    )
)
@PostMapping
public ResponseEntity<ApiObjectResponse> create(@RequestBody ApiObjectRequest request) {
    // Implementation
}
```

## 🐛 Troubleshooting

### Common Issues & Solutions

#### 1. **Java Version Error**
```
Fatal error compiling: java.lang.ExceptionInInitializerError
```

**Solution**: Ensure Java 17 is installed and configured
```bash
java -version  # Should show 17.x.x
export JAVA_HOME=/path/to/java17
```

#### 2. **Port Already in Use**
```
Port 9091 is already in use
```

**Solution**: Kill existing process or change port
```bash
# Kill process on port 9091
lsof -ti:9091 | xargs kill -9

# Or change port in application.properties
server.port=9092
```

#### 3. **H2 Console Not Accessible**
```
404 when accessing /h2-console
```

**Solution**: Verify configuration in `application.properties`
```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

#### 4. **Tests Failing**
```
Connection refused or timeout errors
```

**Solution**: Ensure application is running before tests
```bash
# Start app in one terminal
mvn spring-boot:run

# Run tests in another terminal
mvn test
```

#### 5. **Swagger UI Not Loading**
```
404 on /swagger-ui/index.html
```

**Solution**: Check dependency is included
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

### Debugging Tips

#### Enable Debug Logging
```properties
logging.level.com.api.springboot=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

#### View SQL Queries
Already enabled in `application.properties`:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

#### Check Application Health
```bash
curl http://localhost:9091/
```

#### View All Endpoints
```bash
curl http://localhost:9091/api-docs | jq .
```

## 📚 Additional Resources

### Documentation Files
- [ARCHITECTURE.md](ARCHITECTURE.md) - Detailed architecture diagrams
- [API Documentation](http://localhost:9091/swagger-ui/index.html) - Interactive API docs (when app is running)
- [OpenAPI Spec](http://localhost:9091/api-docs) - Machine-readable API specification

### External Links
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/3.2.0/reference/html/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [REST Assured Documentation](https://rest-assured.io/)
- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [H2 Database Documentation](http://www.h2database.com/html/main.html)

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork the repository**
   ```bash
   git clone https://github.com/krimohshu/springboot-api-test-cucumber.git
   ```

2. **Create feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```

3. **Make your changes**
   - Add tests for new features
   - Update documentation
   - Follow existing code style

4. **Commit changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```

5. **Push to branch**
   ```bash
   git push origin feature/amazing-feature
   ```

6. **Open Pull Request**
   - Describe your changes
   - Reference any related issues
   - Wait for review

### Code Style Guidelines
- Use Lombok annotations to reduce boilerplate
- Follow Spring Boot best practices
- Add Javadoc for public methods
- Include Swagger documentation for new endpoints
- Write BDD tests for new features

## 📄 License

This project is open source and available under the **MIT License**.

```
MIT License

Copyright (c) 2025 krish

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 👤 Author

**Krishan Shukla**
- GitHub: [@krimohshu](https://github.com/krimohshu)
- Repository: [springboot-api-test-cucumber](https://github.com/krimohshu/springboot-api-test-cucumber)

## 🙏 Acknowledgments

- **Spring Boot Team** - Excellent framework and documentation
- **REST Assured Community** - Powerful and intuitive API testing library
- **Cucumber Team** - Making BDD accessible and practical
- **SpringDoc** - Seamless OpenAPI integration for Spring Boot
- **H2 Database** - Fast and reliable in-memory database

## 📊 Project Stats

- **Lines of Code**: ~2,000+
- **Test Coverage**: 15 comprehensive scenarios
- **API Endpoints**: 7 (including documentation endpoints)
- **Technologies**: 13 major frameworks/libraries
- **Java Version**: 17 (LTS)
- **Spring Boot**: 3.2.0 (latest stable)

## 🚀 What's Next?

Future enhancements could include:
- [ ] Authentication & Authorization (Spring Security)
- [ ] Rate Limiting
- [ ] Caching (Redis)
- [ ] Pagination & Sorting
- [ ] File Upload/Download
- [ ] WebSocket Support
- [ ] GraphQL API
- [ ] Docker Containerization
- [ ] Kubernetes Deployment
- [ ] CI/CD Pipeline (GitHub Actions)
- [ ] Monitoring & Metrics (Actuator, Prometheus)
- [ ] API Versioning

---

⭐ **Star this repository** if you find it helpful!

📧 **Questions?** Open an issue or reach out via GitHub.

🔄 **Updates**: Check the [releases page](https://github.com/krimohshu/springboot-api-test-cucumber/releases) for latest updates.
