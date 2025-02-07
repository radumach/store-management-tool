package com.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.context.SpringBootTest;

import com.store.model.Product;
import com.store.repository.ProductRepository;

@SpringBootTest
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void testAddProduct() {
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(1200.0);
        product.setQuantity(10);

        when(productRepository.save(product)).thenReturn(product);

        Product savedProduct = productService.addProduct(product);
        assertNotNull(savedProduct);
        assertEquals("Laptop", savedProduct.getName());
    }
}