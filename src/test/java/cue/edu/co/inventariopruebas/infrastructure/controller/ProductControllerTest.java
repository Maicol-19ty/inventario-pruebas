package cue.edu.co.inventariopruebas.infrastructure.controller;

import cue.edu.co.inventariopruebas.application.dto.ProductDTO;
import cue.edu.co.inventariopruebas.application.dto.ProductRequestDTO;
import cue.edu.co.inventariopruebas.application.service.ProductService;
import cue.edu.co.inventariopruebas.domain.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductDTO productDTO;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Laptop");
        productDTO.setDescription("High performance laptop");
        productDTO.setPrice(new BigDecimal("999.99"));
        productDTO.setStock(10);
        productDTO.setCategoryId(1L);
        productDTO.setCategoryName("Electronics");

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Laptop");
        productRequestDTO.setDescription("High performance laptop");
        productRequestDTO.setPrice(new BigDecimal("999.99"));
        productRequestDTO.setStock(10);
        productRequestDTO.setCategoryId(1L);
    }

    @Test
    @DisplayName("GET /api/products - Should return all products")
    void shouldGetAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(productDTO));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].price").value(999.99));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return product by ID")
    void shouldGetProductById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return 404 when product not found")
    void shouldReturn404WhenProductNotFound() throws Exception {
        when(productService.getProductById(1L))
                .thenThrow(new ResourceNotFoundException("Product", 1L));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /api/products?categoryId=1 - Should return products by category")
    void shouldGetProductsByCategory() throws Exception {
        when(productService.getProductsByCategoryId(1L)).thenReturn(Arrays.asList(productDTO));

        mockMvc.perform(get("/api/products?categoryId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryId").value(1));

        verify(productService, times(1)).getProductsByCategoryId(1L);
    }

    @Test
    @DisplayName("GET /api/products?search=Laptop - Should search products by name")
    void shouldSearchProductsByName() throws Exception {
        when(productService.searchProductsByName("Laptop")).thenReturn(Arrays.asList(productDTO));

        mockMvc.perform(get("/api/products?search=Laptop"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Laptop"));

        verify(productService, times(1)).searchProductsByName("Laptop");
    }

    @Test
    @DisplayName("POST /api/products - Should create product successfully")
    void shouldCreateProduct() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService, times(1)).createProduct(any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/products - Should return 400 when request is invalid")
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        ProductRequestDTO invalidRequest = new ProductRequestDTO();
        invalidRequest.setName("");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any());
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should update product successfully")
    void shouldUpdateProduct() throws Exception {
        when(productService.updateProduct(eq(1L), any(ProductRequestDTO.class))).thenReturn(productDTO);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should delete product successfully")
    void shouldDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("GET /api/products/low-stock - Should return low stock products")
    void shouldGetLowStockProducts() throws Exception {
        when(productService.getLowStockProducts(10)).thenReturn(Arrays.asList(productDTO));

        mockMvc.perform(get("/api/products/low-stock?threshold=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(productService, times(1)).getLowStockProducts(10);
    }
}
