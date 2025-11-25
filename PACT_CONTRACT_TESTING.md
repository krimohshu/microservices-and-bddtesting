# Pact Contract Testing Guide for Microservices

## 📋 Table of Contents
- [What is Contract Testing?](#what-is-contract-testing)
- [Why Pact?](#why-pact)
- [Architecture Overview](#architecture-overview)
- [Implementation Strategy](#implementation-strategy)
- [Pact Broker Setup](#pact-broker-setup)
- [Consumer-Driven Contracts](#consumer-driven-contracts)
- [Provider Verification](#provider-verification)
- [CI/CD Integration](#cicd-integration)
- [Best Practices](#best-practices)
- [Complete Examples](#complete-examples)

---

## 🎯 What is Contract Testing?

Contract testing is a methodology for ensuring that services can communicate with each other. Unlike end-to-end tests, contract tests check that the interfaces between services meet the agreed contract specifications.

```mermaid
graph TB
    subgraph "Traditional Testing Challenges"
        E2E[End-to-End Tests<br/>❌ Slow<br/>❌ Brittle<br/>❌ Complex Setup]
        UNIT[Unit Tests<br/>❌ Don't test integration<br/>❌ Miss interface issues]
    end
    
    subgraph "Contract Testing Solution"
        CONSUMER[Consumer Tests<br/>✅ Fast<br/>✅ Isolated<br/>✅ No Provider Needed]
        PROVIDER[Provider Verification<br/>✅ Independent<br/>✅ No Consumer Needed<br/>✅ Real Implementation]
        CONTRACT[Contract / Pact File]
        
        CONSUMER -->|Generates| CONTRACT
        CONTRACT -->|Verifies Against| PROVIDER
    end
    
    style CONTRACT fill:#90EE90
```

### Key Benefits:
- **Fast Feedback**: Tests run in isolation without starting all services
- **Independent Deployment**: Services can be deployed independently with confidence
- **Clear Contracts**: Explicit documentation of service interactions
- **Prevent Breaking Changes**: Catch incompatibilities before production

---

## 🔍 Why Pact?

Pact is a consumer-driven contract testing framework that enables:

1. **Consumer-Driven**: Consumers define what they need from providers
2. **Language Agnostic**: Supports Java, JavaScript, Python, Go, .NET, etc.
3. **Pact Broker**: Centralized contract storage and verification
4. **Can-I-Deploy**: Pre-deployment compatibility checks
5. **Versioning**: Track contract versions across environments

```mermaid
graph LR
    subgraph "Pact Ecosystem"
        CONSUMER[Consumer<br/>Service]
        PACT_LIB[Pact Library]
        BROKER[Pact Broker]
        PROVIDER[Provider<br/>Service]
        VERIFY[Verification<br/>Results]
        
        CONSUMER -->|1. Write Tests| PACT_LIB
        PACT_LIB -->|2. Generate Contract| BROKER
        BROKER -->|3. Download Contract| PROVIDER
        PROVIDER -->|4. Verify| VERIFY
        VERIFY -->|5. Publish Results| BROKER
    end
    
    style BROKER fill:#FF9900
    style PACT_LIB fill:#90EE90
```

---

## 🏗️ Architecture Overview - Your Microservices

```mermaid
graph TB
    subgraph "Consumer: Order Service"
        ORDER[Order Service<br/>Port: 8083]
        ORDER_PACT[Pact Consumer Tests]
    end
    
    subgraph "Provider: User Service"
        USER[User Service<br/>Port: 8082]
        USER_VERIFY[Pact Provider Verification]
    end
    
    subgraph "Provider: Product Service"
        PRODUCT[Product Service<br/>Port: 8081]
        PRODUCT_VERIFY[Pact Provider Verification]
    end
    
    subgraph "Pact Broker"
        BROKER[Pact Broker<br/>Contract Storage]
        WEBHOOKS[Webhooks<br/>Auto-trigger Verification]
    end
    
    ORDER -->|Calls| USER
    ORDER -->|Calls| PRODUCT
    
    ORDER_PACT -->|Publish Contracts| BROKER
    BROKER -->|Trigger| USER_VERIFY
    BROKER -->|Trigger| PRODUCT_VERIFY
    USER_VERIFY -->|Results| BROKER
    PRODUCT_VERIFY -->|Results| BROKER
    
    style BROKER fill:#FF9900
    style ORDER_PACT fill:#E1F5FF
    style USER_VERIFY fill:#FFE4E1
    style PRODUCT_VERIFY fill:#FFE4E1
```

### Contract Relationships:

| Consumer | Provider | Contract Purpose |
|----------|----------|------------------|
| **Order Service** | **User Service** | Verify user exists before creating order |
| **Order Service** | **Product Service** | Verify product availability and pricing |
| **API Test Suite** | **All Services** | Validate API contracts for testing |

---

## 📦 Implementation Strategy

### Phase 1: Setup Dependencies

Add Pact dependencies to each service's `pom.xml`:

```xml
<!-- Pact Dependencies for Consumer Testing -->
<dependency>
    <groupId>au.com.dius.pact.consumer</groupId>
    <artifactId>junit5</artifactId>
    <version>4.6.3</version>
    <scope>test</scope>
</dependency>

<!-- Pact Dependencies for Provider Verification -->
<dependency>
    <groupId>au.com.dius.pact.provider</groupId>
    <artifactId>junit5spring</artifactId>
    <version>4.6.3</version>
    <scope>test</scope>
</dependency>

<!-- Pact Broker Client -->
<dependency>
    <groupId>au.com.dius.pact</groupId>
    <artifactId>provider</artifactId>
    <version>4.6.3</version>
    <scope>test</scope>
</dependency>
```

### Phase 2: Test Structure

```
order-service/
├── src/
│   └── test/
│       └── java/
│           └── com/microservices/order/
│               ├── contract/
│               │   ├── consumer/
│               │   │   ├── UserServiceConsumerPactTest.java
│               │   │   └── ProductServiceConsumerPactTest.java
│               │   └── provider/
│               │       └── OrderServiceProviderPactTest.java
│               └── pact/
│                   └── PactTestConfig.java

user-service/
├── src/
│   └── test/
│       └── java/
│           └── com/microservices/user/
│               └── contract/
│                   └── provider/
│                       └── UserServiceProviderPactTest.java

product-service/
├── src/
│   └── test/
│       └── java/
│           └── com/microservices/product/
│               └── contract/
│                   └── provider/
│                       └── ProductServiceProviderPactTest.java
```

---

## 🎪 Pact Broker Setup

### Option 1: Docker Compose (Recommended for Local Development)

Create `docker-compose-pact.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: pact-postgres
    environment:
      POSTGRES_USER: pactbroker
      POSTGRES_PASSWORD: pactbroker
      POSTGRES_DB: pactbroker
    ports:
      - "5432:5432"
    volumes:
      - pact-postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U pactbroker"]
      interval: 10s
      timeout: 5s
      retries: 5

  pact-broker:
    image: pactfoundation/pact-broker:latest
    container_name: pact-broker
    depends_on:
      postgres:
        condition: service_healthy
    ports:
      - "9292:9292"
    environment:
      PACT_BROKER_DATABASE_URL: "postgresql://pactbroker:pactbroker@postgres:5432/pactbroker"
      PACT_BROKER_BASIC_AUTH_USERNAME: pactbroker
      PACT_BROKER_BASIC_AUTH_PASSWORD: pactbroker
      PACT_BROKER_ALLOW_PUBLIC_READ: "true"
      PACT_BROKER_DATABASE_CONNECT_MAX_RETRIES: 5
      PACT_BROKER_LOG_LEVEL: INFO
    healthcheck:
      test: ["CMD", "wget", "-q", "--tries=1", "--spider", "http://localhost:9292/diagnostic/status/heartbeat"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  pact-postgres-data:

networks:
  default:
    name: microservices-network
```

**Start Pact Broker:**
```bash
docker-compose -f docker-compose-pact.yml up -d
```

**Access Pact Broker:**
- URL: http://localhost:9292
- Username: pactbroker
- Password: pactbroker

### Option 2: Pactflow (SaaS - Recommended for Production)

PactFlow is a fully managed Pact Broker with additional features:
- No infrastructure management
- Advanced security features
- Better performance
- Professional support

Sign up at: https://pactflow.io/

---

## 👥 Consumer-Driven Contracts

### Example 1: Order Service calling User Service

**File: `order-service/src/test/java/com/microservices/order/contract/consumer/UserServiceConsumerPactTest.java`**

```java
package com.microservices.order.contract.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest
@PactTestFor(providerName = "user-service", pactVersion = PactSpecVersion.V4)
public class UserServiceConsumerPactTest {

    @Pact(consumer = "order-service", provider = "user-service")
    public V4Pact getUserByIdPact(PactDslWithProvider builder) {
        DslPart body = new PactDslJsonBody()
                .stringType("id", "123")
                .stringType("name", "John Doe")
                .stringType("email", "john.doe@example.com")
                .stringType("status", "ACTIVE")
                .stringMatcher("createdAt", "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}", "2024-01-01T10:00:00");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder
                .given("user with id 123 exists")
                .uponReceiving("a request to get user by id")
                    .path("/api/v1/users/123")
                    .method("GET")
                .willRespondWith()
                    .status(200)
                    .headers(headers)
                    .body(body)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getUserByIdPact")
    void testGetUserById_shouldReturnUserDetails(MockServer mockServer) {
        // Arrange
        RestTemplate restTemplate = new RestTemplate();
        String url = mockServer.getUrl() + "/api/v1/users/123";

        // Act
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("id")).isEqualTo("123");
        assertThat(response.getBody().get("name")).isEqualTo("John Doe");
        assertThat(response.getBody().get("email")).isEqualTo("john.doe@example.com");
        assertThat(response.getBody().get("status")).isEqualTo("ACTIVE");
    }

    @Pact(consumer = "order-service", provider = "user-service")
    public V4Pact getUserNotFoundPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        DslPart body = new PactDslJsonBody()
                .stringType("error", "User not found")
                .stringType("message", "User with id 999 does not exist");

        return builder
                .given("user with id 999 does not exist")
                .uponReceiving("a request to get non-existent user")
                    .path("/api/v1/users/999")
                    .method("GET")
                .willRespondWith()
                    .status(404)
                    .headers(headers)
                    .body(body)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getUserNotFoundPact")
    void testGetUserById_shouldReturn404ForNonExistentUser(MockServer mockServer) {
        // Arrange
        RestTemplate restTemplate = new RestTemplate();
        String url = mockServer.getUrl() + "/api/v1/users/999";

        // Act & Assert
        try {
            restTemplate.getForEntity(url, Map.class);
            assertThat(false).as("Should have thrown exception").isTrue();
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
```

### Example 2: Order Service calling Product Service

**File: `order-service/src/test/java/com/microservices/order/contract/consumer/ProductServiceConsumerPactTest.java`**

```java
package com.microservices.order.contract.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest
@PactTestFor(providerName = "product-service", pactVersion = PactSpecVersion.V4)
public class ProductServiceConsumerPactTest {

    @Pact(consumer = "order-service", provider = "product-service")
    public V4Pact getProductByIdPact(PactDslWithProvider builder) {
        DslPart body = new PactDslJsonBody()
                .stringType("id", "P123")
                .stringType("name", "Laptop")
                .stringType("description", "High-performance laptop")
                .decimalType("price", 999.99)
                .integerType("stock", 50)
                .stringType("category", "ELECTRONICS")
                .booleanType("available", true);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder
                .given("product with id P123 exists and is available")
                .uponReceiving("a request to get product by id")
                    .path("/api/v1/products/P123")
                    .method("GET")
                .willRespondWith()
                    .status(200)
                    .headers(headers)
                    .body(body)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getProductByIdPact")
    void testGetProductById_shouldReturnProductDetails(MockServer mockServer) {
        // Arrange
        RestTemplate restTemplate = new RestTemplate();
        String url = mockServer.getUrl() + "/api/v1/products/P123";

        // Act
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("id")).isEqualTo("P123");
        assertThat(response.getBody().get("name")).isEqualTo("Laptop");
        assertThat(response.getBody().get("available")).isEqualTo(true);
    }

    @Pact(consumer = "order-service", provider = "product-service")
    public V4Pact checkProductStockPact(PactDslWithProvider builder) {
        DslPart body = new PactDslJsonBody()
                .stringType("productId", "P123")
                .integerType("availableStock", 50)
                .booleanType("inStock", true);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder
                .given("product P123 has stock available")
                .uponReceiving("a request to check product stock")
                    .path("/api/v1/products/P123/stock")
                    .method("GET")
                .willRespondWith()
                    .status(200)
                    .headers(headers)
                    .body(body)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "checkProductStockPact")
    void testCheckProductStock_shouldReturnStockInfo(MockServer mockServer) {
        // Arrange
        RestTemplate restTemplate = new RestTemplate();
        String url = mockServer.getUrl() + "/api/v1/products/P123/stock";

        // Act
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("productId")).isEqualTo("P123");
        assertThat(response.getBody().get("inStock")).isEqualTo(true);
    }
}
```

### Publishing Contracts to Pact Broker

Add Maven plugin to `order-service/pom.xml`:

```xml
<plugin>
    <groupId>au.com.dius.pact.provider</groupId>
    <artifactId>maven</artifactId>
    <version>4.6.3</version>
    <configuration>
        <pactBrokerUrl>http://localhost:9292</pactBrokerUrl>
        <pactBrokerUsername>pactbroker</pactBrokerUsername>
        <pactBrokerPassword>pactbroker</pactBrokerPassword>
        <pactDirectory>target/pacts</pactDirectory>
        <projectVersion>${project.version}</projectVersion>
        <tags>
            <tag>dev</tag>
        </tags>
    </configuration>
</plugin>
```

**Publish contracts:**
```bash
cd order-service
mvn clean test pact:publish
```

---

## ✅ Provider Verification

### User Service Provider Verification

**File: `user-service/src/test/java/com/microservices/user/contract/provider/UserServiceProviderPactTest.java`**

```java
package com.microservices.user.contract.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import com.microservices.user.controller.UserController;
import com.microservices.user.model.User;
import com.microservices.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@Provider("user-service")
@PactBroker(
    host = "localhost",
    port = "9292",
    authentication = @PactBrokerAuth(username = "pactbroker", password = "pactbroker")
)
public class UserServiceProviderPactTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new MockMvcTestTarget(mockMvc));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("user with id 123 exists")
    public void userWithId123Exists() {
        User user = new User();
        user.setId("123");
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.parse("2024-01-01T10:00:00"));

        Mockito.when(userService.getUserById("123"))
               .thenReturn(Optional.of(user));
    }

    @State("user with id 999 does not exist")
    public void userWithId999DoesNotExist() {
        Mockito.when(userService.getUserById("999"))
               .thenReturn(Optional.empty());
    }

    @State("default")
    public void defaultState() {
        // No setup needed for default state
    }
}
```

### Product Service Provider Verification

**File: `product-service/src/test/java/com/microservices/product/contract/provider/ProductServiceProviderPactTest.java`**

```java
package com.microservices.product.contract.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import com.microservices.product.controller.ProductController;
import com.microservices.product.model.Product;
import com.microservices.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
@Provider("product-service")
@PactBroker(
    host = "localhost",
    port = "9292",
    authentication = @PactBrokerAuth(username = "pactbroker", password = "pactbroker")
)
public class ProductServiceProviderPactTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new MockMvcTestTarget(mockMvc));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("product with id P123 exists and is available")
    public void productP123ExistsAndAvailable() {
        Product product = new Product();
        product.setId("P123");
        product.setName("Laptop");
        product.setDescription("High-performance laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setStock(50);
        product.setCategory("ELECTRONICS");
        product.setAvailable(true);

        Mockito.when(productService.getProductById("P123"))
               .thenReturn(Optional.of(product));
    }

    @State("product P123 has stock available")
    public void productP123HasStock() {
        Map<String, Object> stockInfo = new HashMap<>();
        stockInfo.put("productId", "P123");
        stockInfo.put("availableStock", 50);
        stockInfo.put("inStock", true);

        Mockito.when(productService.checkStock("P123"))
               .thenReturn(stockInfo);
    }

    @State("default")
    public void defaultState() {
        // No setup needed for default state
    }
}
```

### Running Provider Verification

```bash
# For user-service
cd user-service
mvn clean test -Dtest=UserServiceProviderPactTest

# For product-service
cd product-service
mvn clean test -Dtest=ProductServiceProviderPactTest
```

---

## 🔄 CI/CD Integration

### Pact Workflow in CI/CD

```mermaid
sequenceDiagram
    participant Dev as Developer
    participant CI_Consumer as CI: Consumer
    participant Broker as Pact Broker
    participant CI_Provider as CI: Provider
    participant Deploy as Deployment
    participant CanIDeploy as can-i-deploy

    Dev->>CI_Consumer: Push Order Service
    CI_Consumer->>CI_Consumer: Run Consumer Tests
    CI_Consumer->>Broker: Publish Contracts
    Broker->>CI_Provider: Webhook Trigger
    CI_Provider->>Broker: Fetch Contracts
    CI_Provider->>CI_Provider: Run Provider Verification
    CI_Provider->>Broker: Publish Verification Results
    
    Note over Dev,CanIDeploy: Before Deployment
    
    Dev->>CanIDeploy: Request Deployment
    CanIDeploy->>Broker: Check Compatibility
    Broker-->>CanIDeploy: ✅ All Verified
    CanIDeploy->>Deploy: Proceed with Deployment
    Deploy->>Broker: Tag as Deployed (prod)
```

### GitHub Actions Workflow for Consumer

**.github/workflows/pact-consumer-order-service.yml:**

```yaml
name: Pact Contract - Order Service (Consumer)

on:
  push:
    branches: [main, develop]
    paths:
      - 'order-service/**'
  pull_request:
    branches: [main, develop]

env:
  PACT_BROKER_BASE_URL: http://localhost:9292
  PACT_BROKER_USERNAME: pactbroker
  PACT_BROKER_PASSWORD: pactbroker

jobs:
  pact-consumer-tests:
    name: Consumer Contract Tests
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15-alpine
        env:
          POSTGRES_USER: pactbroker
          POSTGRES_PASSWORD: pactbroker
          POSTGRES_DB: pactbroker
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432

      pact-broker:
        image: pactfoundation/pact-broker:latest
        env:
          PACT_BROKER_DATABASE_URL: postgresql://pactbroker:pactbroker@postgres:5432/pactbroker
          PACT_BROKER_BASIC_AUTH_USERNAME: pactbroker
          PACT_BROKER_BASIC_AUTH_PASSWORD: pactbroker
        ports:
          - 9292:9292

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Wait for Pact Broker
        run: |
          for i in {1..30}; do
            if curl -f http://localhost:9292/diagnostic/status/heartbeat; then
              echo "Pact Broker is ready"
              exit 0
            fi
            echo "Waiting for Pact Broker..."
            sleep 5
          done
          echo "Pact Broker failed to start"
          exit 1

      - name: Run Consumer Pact Tests
        working-directory: ./order-service
        run: mvn clean test -Dtest=*ConsumerPactTest

      - name: Publish Pacts to Broker
        working-directory: ./order-service
        run: |
          mvn pact:publish \
            -Dpact.broker.url=${{ env.PACT_BROKER_BASE_URL }} \
            -Dpact.broker.username=${{ env.PACT_BROKER_USERNAME }} \
            -Dpact.broker.password=${{ env.PACT_BROKER_PASSWORD }} \
            -Dpact.consumer.version=${{ github.sha }} \
            -Dpact.consumer.tags=${{ github.ref_name }}

      - name: Upload Pact Files
        uses: actions/upload-artifact@v4
        with:
          name: pacts
          path: order-service/target/pacts/*.json
```

### GitHub Actions Workflow for Provider

**.github/workflows/pact-provider-user-service.yml:**

```yaml
name: Pact Contract - User Service (Provider)

on:
  push:
    branches: [main, develop]
    paths:
      - 'user-service/**'
  repository_dispatch:
    types: [pact-verification-triggered]

env:
  PACT_BROKER_BASE_URL: http://localhost:9292
  PACT_BROKER_USERNAME: pactbroker
  PACT_BROKER_PASSWORD: pactbroker

jobs:
  pact-provider-verification:
    name: Provider Contract Verification
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15-alpine
        env:
          POSTGRES_USER: pactbroker
          POSTGRES_PASSWORD: pactbroker
          POSTGRES_DB: pactbroker
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432

      pact-broker:
        image: pactfoundation/pact-broker:latest
        env:
          PACT_BROKER_DATABASE_URL: postgresql://pactbroker:pactbroker@postgres:5432/pactbroker
          PACT_BROKER_BASIC_AUTH_USERNAME: pactbroker
          PACT_BROKER_BASIC_AUTH_PASSWORD: pactbroker
        ports:
          - 9292:9292

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Wait for Pact Broker
        run: |
          for i in {1..30}; do
            if curl -f http://localhost:9292/diagnostic/status/heartbeat; then
              echo "Pact Broker is ready"
              exit 0
            fi
            echo "Waiting for Pact Broker..."
            sleep 5
          done
          exit 1

      - name: Run Provider Verification Tests
        working-directory: ./user-service
        run: |
          mvn clean test -Dtest=UserServiceProviderPactTest \
            -Dpact.broker.url=${{ env.PACT_BROKER_BASE_URL }} \
            -Dpact.broker.username=${{ env.PACT_BROKER_USERNAME }} \
            -Dpact.broker.password=${{ env.PACT_BROKER_PASSWORD }} \
            -Dpact.provider.version=${{ github.sha }} \
            -Dpact.provider.tag=${{ github.ref_name }}

      - name: Publish Verification Results
        if: always()
        working-directory: ./user-service
        run: |
          mvn pact:verify \
            -Dpact.broker.url=${{ env.PACT_BROKER_BASE_URL }} \
            -Dpact.broker.username=${{ env.PACT_BROKER_USERNAME }} \
            -Dpact.broker.password=${{ env.PACT_BROKER_PASSWORD }} \
            -Dpact.provider.version=${{ github.sha }}
```

### Can-I-Deploy Check

**.github/workflows/can-i-deploy.yml:**

```yaml
name: Can I Deploy?

on:
  workflow_call:
    inputs:
      service:
        required: true
        type: string
      environment:
        required: true
        type: string

jobs:
  can-i-deploy:
    name: Check Deployment Compatibility
    runs-on: ubuntu-latest

    steps:
      - name: Install Pact CLI
        run: |
          curl -fsSL https://raw.githubusercontent.com/pact-foundation/pact-ruby-standalone/master/install.sh | bash

      - name: Can I Deploy?
        run: |
          pact-broker can-i-deploy \
            --pacticipant ${{ inputs.service }} \
            --version ${{ github.sha }} \
            --to-environment ${{ inputs.environment }} \
            --broker-base-url http://localhost:9292 \
            --broker-username pactbroker \
            --broker-password pactbroker

      - name: Record Deployment
        if: success()
        run: |
          pact-broker record-deployment \
            --pacticipant ${{ inputs.service }} \
            --version ${{ github.sha }} \
            --environment ${{ inputs.environment }} \
            --broker-base-url http://localhost:9292 \
            --broker-username pactbroker \
            --broker-password pactbroker
```

---

## 📚 Best Practices

### 1. Contract Design Principles

```mermaid
graph TB
    subgraph "Good Contract Practices"
        SPECIFIC[Be Specific<br/>Define exact expectations]
        MINIMAL[Keep it Minimal<br/>Only test what you use]
        REALISTIC[Use Realistic Data<br/>Match production patterns]
        VERSIONED[Version Everything<br/>Track changes over time]
        INDEPENDENT[Test Independence<br/>Each test self-contained]
    end
    
    style SPECIFIC fill:#90EE90
    style MINIMAL fill:#90EE90
    style REALISTIC fill:#90EE90
    style VERSIONED fill:#90EE90
    style INDEPENDENT fill:#90EE90
```

### 2. State Management

- **Provider States should be:**
  - Descriptive and unique
  - Idempotent (can run multiple times)
  - Independent of each other
  - Realistic to production scenarios

```java
@State("product with id P123 exists with low stock")
public void productP123HasLowStock() {
    // Setup: Product exists but stock < 10
    Product product = createProduct("P123", 5);
    mockProductService(product);
}

@State("product with id P456 is out of stock")
public void productP456OutOfStock() {
    // Setup: Product exists but stock = 0
    Product product = createProduct("P456", 0);
    mockProductService(product);
}
```

### 3. Contract Evolution

```mermaid
graph LR
    V1[Contract v1.0] -->|Add Optional Field| V1_1[Contract v1.1]
    V1_1 -->|Add New Endpoint| V2[Contract v2.0]
    V2 -->|Breaking Change| V3[Contract v3.0]
    
    style V1 fill:#90EE90
    style V1_1 fill:#90EE90
    style V2 fill:#FFD700
    style V3 fill:#FFB6C1
```

**Rules:**
- ✅ **Non-Breaking**: Add optional fields, add new endpoints
- ❌ **Breaking**: Remove fields, change field types, change URLs
- 📦 **Version properly**: Use semantic versioning for contracts

### 4. Testing Strategy

| Test Type | When to Use | Example |
|-----------|-------------|---------|
| **Consumer Tests** | Before every commit | Order service calling User API |
| **Provider Verification** | On provider changes, webhook trigger | User service validates contracts |
| **Can-I-Deploy** | Before deployment | Check compatibility matrix |
| **Smoke Tests** | After deployment | Quick sanity check |

### 5. Pact Broker Organization

```
Pact Broker
├── Pacticipants (Services)
│   ├── order-service (Consumer)
│   ├── user-service (Provider)
│   └── product-service (Provider)
├── Contracts (Pacts)
│   ├── order-service → user-service
│   └── order-service → product-service
├── Verifications
│   ├── user-service verified (✅)
│   └── product-service verified (✅)
└── Environments
    ├── dev
    ├── staging
    └── production
```

---

## 🎓 Complete Implementation Checklist

### Week 1: Setup
- [ ] Add Pact dependencies to all services
- [ ] Setup Pact Broker (Docker Compose or PactFlow)
- [ ] Configure Maven Pact plugin
- [ ] Create test directory structure

### Week 2: Consumer Tests
- [ ] Write consumer tests for Order → User Service
- [ ] Write consumer tests for Order → Product Service
- [ ] Run tests locally and verify pact files generated
- [ ] Publish pacts to broker manually

### Week 3: Provider Verification
- [ ] Create provider verification tests for User Service
- [ ] Create provider verification tests for Product Service
- [ ] Setup provider states
- [ ] Run verifications locally

### Week 4: CI/CD Integration
- [ ] Create GitHub Actions workflow for consumers
- [ ] Create GitHub Actions workflow for providers
- [ ] Setup Pact Broker webhooks
- [ ] Implement can-i-deploy checks

### Week 5: Production Readiness
- [ ] Document contract testing process
- [ ] Train team on Pact workflow
- [ ] Setup monitoring and alerts
- [ ] Implement contract versioning strategy

---

## 📊 Benefits Summary

```mermaid
graph TB
    subgraph "Without Pact"
        E2E_SLOW[Slow E2E Tests<br/>30+ minutes]
        E2E_FLAKY[Flaky Tests<br/>Environment issues]
        E2E_LATE[Late Feedback<br/>Post-deployment]
    end
    
    subgraph "With Pact"
        FAST[Fast Tests<br/>< 5 minutes]
        RELIABLE[Reliable Tests<br/>Mock providers]
        EARLY[Early Feedback<br/>Pre-deployment]
        CONFIDENCE[Deployment Confidence<br/>can-i-deploy]
    end
    
    E2E_SLOW -.->|Transform| FAST
    E2E_FLAKY -.->|Transform| RELIABLE
    E2E_LATE -.->|Transform| EARLY
    
    style FAST fill:#90EE90
    style RELIABLE fill:#90EE90
    style EARLY fill:#90EE90
    style CONFIDENCE fill:#90EE90
```

---

## 🔗 Additional Resources

- **Pact Documentation**: https://docs.pact.io/
- **Pact JVM**: https://github.com/pact-foundation/pact-jvm
- **Pact Broker**: https://github.com/pact-foundation/pact_broker
- **PactFlow (SaaS)**: https://pactflow.io/
- **Pact Workshop**: https://docs.pact.io/implementation_guides/workshops

---

**Author**: Krishan Shukla  
**Date**: November 2025  
**Version**: 1.0
