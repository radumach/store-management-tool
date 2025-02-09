package com.store.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.model.Product;
import com.store.model.User;
import com.store.repository.ProductRepository;
import com.store.repository.UserRepository;
import com.store.security.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        productRepository.deleteAll();

        User admin = new User("admin", passwordEncoder.encode("adminpassword"), "ROLE_ADMIN");
        userRepository.save(admin);

        User user = new User("user", passwordEncoder.encode("userpassword"), "ROLE_USER");
        userRepository.save(user);

        adminToken = jwtUtil.generateToken(userDetailsService.loadUserByUsername("admin"));
        userToken = jwtUtil.generateToken(userDetailsService.loadUserByUsername("user"));
    }

    @Test
    public void testAddProduct_AdminRole_Success() throws Exception {
        Product product = new Product("Smartphone", 800.0, 10);

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateProductPrice_AdminRole_Success() throws Exception {
        Product product = productRepository.save(new Product("Laptop", 1200.0, 10));

        mockMvc.perform(put("/api/products/" + product.getId() + "/price?newPrice=1000.0")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(1000.0));
    }

    @Test
    public void testFindProductById_UserRole_Success() throws Exception {
        Product product = productRepository.save(new Product("Keyboard", 50.0, 100));

        mockMvc.perform(get("/api/products/" + product.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Keyboard"));
    }

    @Test
    public void testGetAllProducts_AdminRole_Success() throws Exception {
        productRepository.save(new Product("Mouse", 20.0, 200));

        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }
}
