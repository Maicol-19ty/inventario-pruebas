package cue.edu.co.inventariopruebas.integration;

import cue.edu.co.inventariopruebas.application.dto.CategoryRequestDTO;
import cue.edu.co.inventariopruebas.application.dto.ProductRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long categoryId;

    @BeforeEach
    void setUp() throws Exception {
        // Create a category for testing
        CategoryRequestDTO categoryRequest = new CategoryRequestDTO("Electronics Integration Test");

        String response = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        categoryId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @DisplayName("Integration: Should create, read, update and delete product")
    void shouldPerformFullCrudOnProduct() throws Exception {
        // Create
        ProductRequestDTO createRequest = new ProductRequestDTO();
        createRequest.setName("Test Laptop");
        createRequest.setDescription("High performance laptop for testing");
        createRequest.setPrice(new BigDecimal("1299.99"));
        createRequest.setStock(5);
        createRequest.setCategoryId(categoryId);

        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Laptop"))
                .andExpect(jsonPath("$.price").value(1299.99))
                .andExpect(jsonPath("$.stock").value(5))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long productId = objectMapper.readTree(response).get("id").asLong();

        // Read
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Test Laptop"))
                .andExpect(jsonPath("$.categoryId").value(categoryId));

        // Update
        ProductRequestDTO updateRequest = new ProductRequestDTO();
        updateRequest.setName("Updated Laptop");
        updateRequest.setDescription("Updated description");
        updateRequest.setPrice(new BigDecimal("1099.99"));
        updateRequest.setStock(10);
        updateRequest.setCategoryId(categoryId);

        mockMvc.perform(put("/api/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.price").value(1099.99))
                .andExpect(jsonPath("$.stock").value(10));

        // Delete
        mockMvc.perform(delete("/api/products/" + productId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Integration: Should validate product fields")
    void shouldValidateProductFields() throws Exception {
        // Invalid price (negative)
        ProductRequestDTO invalidPrice = new ProductRequestDTO();
        invalidPrice.setName("Test Product");
        invalidPrice.setPrice(new BigDecimal("-10.00"));
        invalidPrice.setStock(5);
        invalidPrice.setCategoryId(categoryId);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPrice)))
                .andExpect(status().isBadRequest());

        // Invalid stock (negative)
        ProductRequestDTO invalidStock = new ProductRequestDTO();
        invalidStock.setName("Test Product");
        invalidStock.setPrice(new BigDecimal("10.00"));
        invalidStock.setStock(-5);
        invalidStock.setCategoryId(categoryId);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStock)))
                .andExpect(status().isBadRequest());

        // Missing required fields
        ProductRequestDTO missingFields = new ProductRequestDTO();
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missingFields)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration: Should filter products by category")
    void shouldFilterProductsByCategory() throws Exception {
        // Create product
        ProductRequestDTO product = new ProductRequestDTO();
        product.setName("Filtered Product");
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(10);
        product.setCategoryId(categoryId);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());

        // Filter by category
        mockMvc.perform(get("/api/products?categoryId=" + categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.categoryId == " + categoryId + ")]").exists());
    }

    @Test
    @DisplayName("Integration: Should search products by name")
    void shouldSearchProductsByName() throws Exception {
        // Create product
        ProductRequestDTO product = new ProductRequestDTO();
        product.setName("Searchable Laptop");
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("999.99"));
        product.setStock(10);
        product.setCategoryId(categoryId);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());

        // Search by name
        mockMvc.perform(get("/api/products?search=Searchable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.name =~ /.*Searchable.*/i)]").exists());
    }

    @Test
    @DisplayName("Integration: Should get all products")
    void shouldGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Integration: Should fail when creating product with non-existent category")
    void shouldFailWhenCreatingProductWithNonExistentCategory() throws Exception {
        ProductRequestDTO product = new ProductRequestDTO();
        product.setName("Test Product");
        product.setDescription("Test");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(10);
        product.setCategoryId(9999L); // Non-existent category

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound());
    }
}
