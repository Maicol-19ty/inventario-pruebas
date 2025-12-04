package cue.edu.co.inventariopruebas.infrastructure.controller;

import cue.edu.co.inventariopruebas.application.dto.CategoryDTO;
import cue.edu.co.inventariopruebas.application.dto.CategoryRequestDTO;
import cue.edu.co.inventariopruebas.application.service.CategoryService;
import cue.edu.co.inventariopruebas.domain.exception.DuplicateResourceException;
import cue.edu.co.inventariopruebas.domain.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private CategoryDTO categoryDTO;
    private CategoryRequestDTO categoryRequestDTO;

    @BeforeEach
    void setUp() {
        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Electronics");

        categoryRequestDTO = new CategoryRequestDTO("Electronics");
    }

    @Test
    @DisplayName("GET /api/categories - Should return all categories")
    void shouldGetAllCategories() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(categoryDTO));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @DisplayName("GET /api/categories/{id} - Should return category by ID")
    void shouldGetCategoryById() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(categoryDTO);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    @DisplayName("GET /api/categories/{id} - Should return 404 when category not found")
    void shouldReturn404WhenCategoryNotFound() throws Exception {
        when(categoryService.getCategoryById(1L))
                .thenThrow(new ResourceNotFoundException("Category", 1L));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    @DisplayName("POST /api/categories - Should create category successfully")
    void shouldCreateCategory() throws Exception {
        when(categoryService.createCategory(any(CategoryRequestDTO.class))).thenReturn(categoryDTO);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService, times(1)).createCategory(any(CategoryRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/categories - Should return 409 when category name already exists")
    void shouldReturn409WhenCategoryNameExists() throws Exception {
        when(categoryService.createCategory(any(CategoryRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Category", "name", "Electronics"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().isConflict());

        verify(categoryService, times(1)).createCategory(any(CategoryRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/categories - Should return 400 when request is invalid")
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        CategoryRequestDTO invalidRequest = new CategoryRequestDTO("");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any());
    }

    @Test
    @DisplayName("PUT /api/categories/{id} - Should update category successfully")
    void shouldUpdateCategory() throws Exception {
        when(categoryService.updateCategory(eq(1L), any(CategoryRequestDTO.class))).thenReturn(categoryDTO);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} - Should delete category successfully")
    void shouldDeleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }
}
