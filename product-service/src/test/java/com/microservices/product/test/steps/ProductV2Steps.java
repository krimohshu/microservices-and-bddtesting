package com.microservices.product.test.steps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.microservices.product.test.context.TestContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Step Definitions for Product Service V2 API (Enhanced Features)
 */
@Slf4j
@RequiredArgsConstructor
public class ProductV2Steps {
    
    private final TestContext testContext;
    
    @Given("I generate a random SKU")
    public void iGenerateARandomSKU() {
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v2/products/generate-sku");
        
        assertThat("SKU generation should succeed", response.getStatusCode(), equalTo(200));
        
        String generatedSku = response.jsonPath().getString("sku");
        testContext.setData("generatedSku", generatedSku);
        
        log.info("Generated SKU: {}", generatedSku);
    }
    
    @Given("I create a V2 product with SKU {string} and category {string}")
    public void iCreateAV2ProductWithSKUAndCategory(String sku, String category) {
        String requestBody = String.format("""
            {
                "name": "Test Product",
                "description": "Test product for V2 API",
                "price": 99.99,
                "stock": 20,
                "sku": "%s",
                "category": "%s",
                "tags": ["test"],
                "active": true
            }
            """, sku, category);
        
        log.info("Creating V2 product with SKU: {} and category: {}", sku, category);
        
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v2/products");
        
        assertThat("Product creation should succeed", response.getStatusCode(), equalTo(201));
        
        Long productId = response.jsonPath().getLong("id");
        testContext.setData("productId", productId);
        testContext.setResponse(response);
        
        log.info("Created V2 product with ID: {}", productId);
    }
    
    @Given("I create a V2 product with SKU {string}")
    public void iCreateAV2ProductWithSKU(String sku) {
        iCreateAV2ProductWithSKUAndCategory(sku, "Test");
    }
    
    @Given("I create {int} products in category {string}")
    public void iCreateProductsInCategory(int count, String category) {
        log.info("Creating {} products in category: {}", count, category);
        
        for (int i = 0; i < count; i++) {
            String sku = generateRandomSKU();
            double price = 100.0 + (i * 50.0);
            
            String requestBody = String.format("""
                {
                    "name": "Product %d",
                    "description": "Test product %d",
                    "price": %.2f,
                    "stock": %d,
                    "sku": "%s",
                    "category": "%s",
                    "tags": ["test"],
                    "active": true
                }
                """, i + 1, i + 1, price, 10 + i, sku, category);
            
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/api/v2/products");
            
            assertThat("Product creation should succeed", response.getStatusCode(), equalTo(201));
        }
        
        log.info("Created {} products successfully", count);
    }
    
    @Given("I create a V2 product with name {string} price {double} category {string}")
    public void iCreateAV2ProductWithNamePriceCategory(String name, double price, String category) {
        String sku = generateRandomSKU();
        
        String requestBody = String.format("""
            {
                "name": "%s",
                "description": "Test product",
                "price": %.2f,
                "stock": 15,
                "sku": "%s",
                "category": "%s",
                "tags": ["test"],
                "active": true
            }
            """, name, price, sku, category);
        
        log.info("Creating V2 product: {} with price: {} and category: {}", name, price, category);
        
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v2/products");
        
        assertThat("Product creation should succeed", response.getStatusCode(), equalTo(201));
        
        Long productId = response.jsonPath().getLong("id");
        testContext.setData("productId", productId);
        testContext.setData("lastCreatedProductName", name);
        
        log.info("Created V2 product with ID: {}", productId);
    }
    
    @Given("I create a V2 product with tags {}")
    public void iCreateAV2ProductWithTags(List<String> tags) {
        String sku = generateRandomSKU();
        String tagsJson = String.format("[\"%s\"]", String.join("\", \"", tags));
        
        String requestBody = String.format("""
            {
                "name": "Product with Tags",
                "description": "Test product with tags",
                "price": 79.99,
                "stock": 12,
                "sku": "%s",
                "category": "Test",
                "tags": %s,
                "active": true
            }
            """, sku, tagsJson);
        
        log.info("Creating V2 product with tags: {}", tags);
        
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v2/products");
        
        assertThat("Product creation should succeed", response.getStatusCode(), equalTo(201));
        
        Long productId = response.jsonPath().getLong("id");
        testContext.setData("productId", productId);
        
        log.info("Created V2 product with tags, ID: {}", productId);
    }
    
    @Given("I have a list of {int} products to create in bulk")
    public void iHaveAListOfProductsToCreateInBulk(int count) {
        List<Map<String, Object>> products = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("name", "Bulk Product " + (i + 1));
            product.put("description", "Bulk test product " + (i + 1));
            product.put("price", 50.0 + (i * 10.0));
            product.put("stock", 5 + i);
            product.put("sku", generateRandomSKU());
            product.put("category", "Bulk");
            product.put("tags", List.of("bulk", "test"));
            product.put("active", true);
            
            products.add(product);
        }
        
        testContext.setData("bulkProducts", products);
        log.info("Prepared {} products for bulk creation", count);
    }
    
    @When("I send a POST request to {string} with the bulk product list")
    public void iSendAPostRequestToWithTheBulkProductList(String endpoint) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) testContext.getData("bulkProducts");
        
        Map<String, Object> bulkRequest = new HashMap<>();
        bulkRequest.put("products", products);
        
        log.info("Sending POST request to: {} with {} products", endpoint, products.size());
        
        Response response = given()
                .contentType(ContentType.JSON)
                .body(bulkRequest)
                .when()
                .post(endpoint);
        
        testContext.setResponse(response);
        log.info("Response status: {}", response.getStatusCode());
    }
    
    @Given("I create a V2 product with name {string} and category {string}")
    public void iCreateAV2ProductWithNameAndCategory(String name, String category) {
        String sku = generateRandomSKU();
        
        String requestBody = String.format("""
            {
                "name": "%s",
                "description": "Test product",
                "price": 49.99,
                "stock": 10,
                "sku": "%s",
                "category": "%s",
                "tags": ["test"],
                "active": true
            }
            """, name, sku, category);
        
        log.info("Creating V2 product: {} in category: {}", name, category);
        
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v2/products");
        
        assertThat("Product creation should succeed", response.getStatusCode(), equalTo(201));
        
        Long productId = response.jsonPath().getLong("id");
        testContext.setData("productId", productId);
        
        log.info("Created V2 product with ID: {}", productId);
    }
    
    @Given("I create a V2 product with category {string}")
    public void iCreateAV2ProductWithCategory(String category) {
        iCreateAV2ProductWithNameAndCategory("Product in " + category, category);
    }
    
    @Given("I create {int} products with different prices in category {string}")
    public void iCreateProductsWithDifferentPricesInCategory(int count, String category) {
        log.info("Creating {} products with different prices in category: {}", count, category);
        
        double[] prices = {99.99, 149.99, 249.99};
        
        for (int i = 0; i < count; i++) {
            String sku = generateRandomSKU();
            double price = prices[i % prices.length] + (i * 10.0);
            
            String requestBody = String.format("""
                {
                    "name": "Product %d",
                    "description": "Test product %d",
                    "price": %.2f,
                    "stock": %d,
                    "sku": "%s",
                    "category": "%s",
                    "tags": ["test"],
                    "active": true
                }
                """, i + 1, i + 1, price, 10 + i, sku, category);
            
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/api/v2/products");
            
            assertThat("Product creation should succeed", response.getStatusCode(), equalTo(201));
        }
        
        log.info("Created {} products with different prices", count);
    }
    
    @Given("I create a V2 product with stock {int}")
    public void iCreateAV2ProductWithStock(int stock) {
        String sku = generateRandomSKU();
        
        String requestBody = String.format("""
            {
                "name": "Stock Test Product",
                "description": "Product for stock testing",
                "price": 59.99,
                "stock": %d,
                "sku": "%s",
                "category": "Test",
                "tags": ["test"],
                "active": true
            }
            """, stock, sku);
        
        log.info("Creating V2 product with stock: {}", stock);
        
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v2/products");
        
        assertThat("Product creation should succeed", response.getStatusCode(), equalTo(201));
        
        log.info("Created V2 product with stock: {}", stock);
    }
    
    @Then("the response should have pagination info")
    public void theResponseShouldHavePaginationInfo() {
        Response response = testContext.getResponse();
        
        assertThat("Should have 'content' field", response.jsonPath().get("content"), notNullValue());
        assertThat("Should have 'totalElements' field", response.jsonPath().get("totalElements"), notNullValue());
        assertThat("Should have 'totalPages' field", response.jsonPath().get("totalPages"), notNullValue());
        assertThat("Should have 'size' field", response.jsonPath().get("size"), notNullValue());
        assertThat("Should have 'number' field", response.jsonPath().get("number"), notNullValue());
        
        log.info("Verified response has pagination info");
    }
    
    @Then("the pagination {string} should be at least {int}")
    public void thePaginationShouldBeAtLeast(String field, int minValue) {
        Response response = testContext.getResponse();
        int actualValue = response.jsonPath().getInt(field);
        
        assertThat(String.format("Pagination field '%s'", field), actualValue, greaterThanOrEqualTo(minValue));
        log.info("Verified pagination field '{}' = {} (>= {})", field, actualValue, minValue);
    }
    
    @Then("the pagination {string} should be {int}")
    public void thePaginationShouldBe(String field, int expectedValue) {
        Response response = testContext.getResponse();
        int actualValue = response.jsonPath().getInt(field);
        
        assertThat(String.format("Pagination field '%s'", field), actualValue, equalTo(expectedValue));
        log.info("Verified pagination field '{}' = {}", field, expectedValue);
    }
    
    @Then("the response content should contain product with name {string}")
    public void theResponseContentShouldContainProductWithName(String expectedName) {
        Response response = testContext.getResponse();
        List<Map<String, Object>> products = response.jsonPath().getList("content");
        
        boolean found = products.stream()
                .anyMatch(product -> expectedName.equals(product.get("name")));
        
        assertThat(String.format("Response content should contain product with name '%s'", expectedName), 
                   found, is(true));
        log.info("Verified response content contains product with name: {}", expectedName);
    }
    
    @Then("the response content should not contain product with name {string}")
    public void theResponseContentShouldNotContainProductWithName(String unexpectedName) {
        Response response = testContext.getResponse();
        List<Map<String, Object>> products = response.jsonPath().getList("content");
        
        boolean found = products.stream()
                .anyMatch(product -> unexpectedName.equals(product.get("name")));
        
        assertThat(String.format("Response content should NOT contain product with name '%s'", unexpectedName), 
                   found, is(false));
        log.info("Verified response content does NOT contain product with name: {}", unexpectedName);
    }
    
    @Then("the response field {string} should contain {string}")
    public void theResponseFieldShouldContain(String fieldName, String expectedValue) {
        Response response = testContext.getResponse();
        List<String> fieldValues = response.jsonPath().getList(fieldName);
        
        assertThat(String.format("Field '%s' should contain value", fieldName), 
                   fieldValues, hasItem(expectedValue));
        log.info("Verified field '{}' contains value: {}", fieldName, expectedValue);
    }
    
    @Then("all products in response should have stock greater than {int}")
    public void allProductsInResponseShouldHaveStockGreaterThan(int minStock) {
        Response response = testContext.getResponse();
        List<Map<String, Object>> products = response.jsonPath().getList("content");
        
        for (Map<String, Object> product : products) {
            Integer stock = (Integer) product.get("stock");
            assertThat(String.format("Product '%s' stock", product.get("name")), 
                       stock, greaterThan(minStock));
        }
        
        log.info("Verified all products have stock > {}", minStock);
    }
    
    // Helper method - generates SKU max 20 chars (PRD-XXXXX-XXXX format)
    private String generateRandomSKU() {
        // Use last 5 digits of timestamp + 4 digit random = PRD-12345-6789 (14 chars)
        long timestamp = System.currentTimeMillis() % 100000; // last 5 digits
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return String.format("PRD-%05d-%04d", timestamp, random);
    }
}
