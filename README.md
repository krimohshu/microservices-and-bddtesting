# Spring Boot Microservices Workspace

This workspace contains multiple Spring Boot microservices with API versioning (v1 and v2).

## 📁 Projects

### 1. **springboot-api-test-cucumber** (Port 9091)
- Original API testing application
- Full CRUD operations with Cucumber BDD tests
- Swagger UI: http://localhost:9091/swagger-ui/index.html

### 2. **product-service** (Port 8081)
- Product catalog microservice
- **v1**: Basic CRUD (Production)
- **v2**: Enhanced with categories & ratings (QA)
- Swagger UI: http://localhost:8081/swagger-ui/index.html

### 3. **user-service** (Port 8082)
- User management microservice
- **v1**: Basic user profiles (Production)
- **v2**: Enhanced with roles & permissions (QA)
- Swagger UI: http://localhost:8082/swagger-ui/index.html

### 4. **order-service** (Port 8083)
- Order management microservice
- **v1**: Basic order operations (Production)
- **v2**: Enhanced with status tracking (QA)
- Swagger UI: http://localhost:8083/swagger-ui/index.html

### 5. **microservices-common**
- Shared utilities and test frameworks
- Common DTOs and exceptions

## 🚀 Quick Start

### Open Workspace
```bash
# From VS Code: File > Open Workspace from File
# Select: springboot-microservices.code-workspace
```

Or from command line:
```bash
code springboot-microservices.code-workspace
```

### Run All Services
```bash
cd scripts
./start-all.sh
```

### Run Individual Service
```bash
cd product-service
mvn spring-boot:run
```

## 🔧 VS Code Features

### Run from Debug Panel
1. Open **Run and Debug** (Cmd+Shift+D)
2. Select service from dropdown
3. Click **Start Debugging** (F5)

### Run All Services Together
1. Select **🚀 All Microservices** compound
2. Click **Start Debugging**
3. All services start simultaneously

### Search Across All Projects
- **Cmd+Shift+F**: Search in all folders
- Useful for finding similar implementations

## 📊 Port Mapping

| Service | Port | v1 Endpoint | v2 Endpoint |
|---------|------|-------------|-------------|
| Product Service | 8081 | /api/v1/products | /api/v2/products |
| User Service | 8082 | /api/v1/users | /api/v2/users |
| Order Service | 8083 | /api/v1/orders | /api/v2/orders |
| API Test App | 9091 | /api/objects | - |

## 🧪 Testing

Each service has Cucumber BDD tests:
```bash
cd product-service
mvn test
```

## 📚 Documentation

- Architecture diagrams in each service's `ARCHITECTURE.md`
- API documentation via Swagger UI
- Test scenarios in `src/test/resources/features/`

## 🛠️ Development Tips

1. **Java 17 Required**: All services use Java 17
2. **Maven 3.9+**: Ensure Maven is installed
3. **H2 Console**: Each service has its own H2 database
4. **Auto-Import**: VS Code will auto-import dependencies

## 📝 Next Steps

1. ✅ Product Service v1 complete
2. 🔄 Add Product Service v2
3. 🔄 Create User Service
4. 🔄 Create Order Service
5. 🔄 Write Cucumber tests
6. 🔄 Add Docker support
