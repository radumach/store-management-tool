package com.store.integration;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.store.model.Product;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private HttpHeaders createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return headers;
    }

    // Helper method to create a product
    private Product createProduct(String name, double price, int quantity) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
        return product;
    }

    // Positive Test: ADMIN adds a product
    @Test
    public void testAddProduct_AdminRole_Success() {
        Product product = createProduct("Smartphone", 800.0, 10);
        HttpEntity<Product> request = new HttpEntity<>(product, createHeaders("admin", "admin"));

        ResponseEntity<Product> response = restTemplate.postForEntity("/api/products", request, Product.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Smartphone", response.getBody().getName());
    }

    // Negative Test: USER tries to add a product (unauthorized)
    @Test
    public void testAddProduct_UserRole_Unauthorized() {
        Product product = createProduct("Tablet", 500.0, 5);
        HttpEntity<Product> request = new HttpEntity<>(product, createHeaders("user", "password"));

        ResponseEntity<String> response = restTemplate.postForEntity("/api/products", request, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // Positive Test: ADMIN updates product price
    @Test
    public void testUpdateProductPrice_AdminRole_Success() {
        // First, add a product
        Product product = createProduct("Laptop", 1200.0, 10);
        HttpEntity<Product> addRequest = new HttpEntity<>(product, createHeaders("admin", "admin"));
        ResponseEntity<Product> addResponse = restTemplate.postForEntity("/api/products", addRequest, Product.class);
        Long productId = addResponse.getBody().getId();

        // Update the price
        HttpEntity<Void> updateRequest = new HttpEntity<>(createHeaders("admin", "admin"));
        ResponseEntity<Product> updateResponse = restTemplate.exchange(
                "/api/products/" + productId + "/price?newPrice=1000.0",
                HttpMethod.PUT,
                updateRequest,
                Product.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals(1000.0, updateResponse.getBody().getPrice());
    }

    // Negative Test: USER tries to update product price (unauthorized)
    @Test
    public void testUpdateProductPrice_UserRole_Unauthorized() {
        // First, add a product
        Product product = createProduct("Monitor", 300.0, 20);
        HttpEntity<Product> addRequest = new HttpEntity<>(product, createHeaders("admin", "admin"));
        ResponseEntity<Product> addResponse = restTemplate.postForEntity("/api/products", addRequest, Product.class);
        Long productId = addResponse.getBody().getId();

        // Attempt to update the price as USER
        HttpEntity<Void> updateRequest = new HttpEntity<>(createHeaders("user", "password"));
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                "/api/products/" + productId + "/price?newPrice=250.0",
                HttpMethod.PUT,
                updateRequest,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, updateResponse.getStatusCode());
    }

    // Positive Test: USER finds a product by ID
    @Test
    public void testFindProductById_UserRole_Success() {
        // First, add a product
        Product product = createProduct("Keyboard", 50.0, 100);
        HttpEntity<Product> addRequest = new HttpEntity<>(product, createHeaders("admin", "admin"));
        ResponseEntity<Product> addResponse = restTemplate.postForEntity("/api/products", addRequest, Product.class);
        Long productId = addResponse.getBody().getId();

        // Find the product as USER
        HttpEntity<Void> findRequest = new HttpEntity<>(createHeaders("user", "password"));
        ResponseEntity<Product> findResponse = restTemplate.exchange(
                "/api/products/" + productId,
                HttpMethod.GET,
                findRequest,
                Product.class);

        assertEquals(HttpStatus.OK, findResponse.getStatusCode());
        assertEquals("Keyboard", findResponse.getBody().getName());
    }

    // Negative Test: Find a non-existent product
    @Test
    public void testFindProductById_NotFound() {
        HttpEntity<Void> request = new HttpEntity<>(createHeaders("user", "password"));
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/products/999",
                HttpMethod.GET,
                request,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Positive Test: ADMIN gets all products
    @Test
    public void testGetAllProducts_AdminRole_Success() {
        // Add a product
        Product product = createProduct("Mouse", 20.0, 200);
        HttpEntity<Product> addRequest = new HttpEntity<>(product, createHeaders("admin", "admin"));
        restTemplate.postForEntity("/api/products", addRequest, Product.class);

        // Get all products as ADMIN
        HttpEntity<Void> request = new HttpEntity<>(createHeaders("admin", "admin"));
        ResponseEntity<Product[]> response = restTemplate.exchange(
                "/api/products",
                HttpMethod.GET,
                request,
                Product[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
    }

    // Negative Test: Unauthenticated user tries to access endpoints
    @Test
    public void testUnauthenticatedAccess() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/products", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testAddProduct_ValidInput_Success() {
        Product product = createProduct("Smartphone", 800.0, 10);
        HttpEntity<Product> request = new HttpEntity<>(product, createHeaders("admin", "admin"));

        ResponseEntity<Product> response = restTemplate.postForEntity("/api/products", request, Product.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Smartphone", response.getBody().getName());
    }

    @Test
    public void testAddProduct_InvalidName_BadRequest() {
        Product product = createProduct("", 800.0, 10); // Empty name
        HttpEntity<Product> request = new HttpEntity<>(product, createHeaders("admin", "admin"));

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/products", request, Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("name"));
        assertEquals("Product name must contain only alphanumeric characters and spaces", response.getBody().get("name"));
    }

    @Test
    public void testAddProduct_InvalidPrice_BadRequest() {
        Product product = createProduct("Smartphone", -100.0, 10); // Negative price
        HttpEntity<Product> request = new HttpEntity<>(product, createHeaders("admin", "admin"));

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/products", request, Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("price"));
        assertEquals("Price must be a positive number", response.getBody().get("price"));
    }

    @Test
    public void testAddProduct_InvalidQuantity_BadRequest() {
        Product product = createProduct("Smartphone", 800.0, -5); // Negative quantity
        HttpEntity<Product> request = new HttpEntity<>(product, createHeaders("admin", "admin"));

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/products", request, Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("quantity"));
        assertEquals("Quantity must be greater than or equal to 0", response.getBody().get("quantity"));
    }

    @Test
    public void testAddProduct_InvalidInput_BadRequest() {
        Product product = createProduct("<script>alert('XSS')</script>", -100.0, -5); // Malicious input
        HttpEntity<Product> request = new HttpEntity<>(product, createHeaders("admin", "admin"));

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/products", request, Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("name"));
        assertTrue(response.getBody().containsKey("price"));
        assertTrue(response.getBody().containsKey("quantity"));
    }

    /**
     * This test is disabled because it requires CSRF protection to be enabled.
     */
    @Test
    @Disabled
    public void testAddProduct_CsrfProtection() {
        Product product = createProduct("Laptop", 1200.0, 10);
        HttpEntity<Product> request = new HttpEntity<>(product, createHeaders("admin", "admin"));

        // Disable CSRF token
        restTemplate.getRestTemplate().setInterceptors(Collections.emptyList());

        ResponseEntity<String> response = restTemplate.postForEntity("/api/products", request, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}