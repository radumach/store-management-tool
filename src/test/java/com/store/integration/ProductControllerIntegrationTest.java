package com.store.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.store.model.Product;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testAddProduct() {
        Product product = new Product();
        product.setName("Smartphone");
        product.setPrice(800.0);
        product.setQuantity(5);

        ResponseEntity<Product> response = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/api/products", product, Product.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Smartphone", response.getBody().getName());
    }
}