package cue.edu.co.inventariopruebas.integration;

import cue.edu.co.inventariopruebas.application.dto.CategoryRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Integration: Should create, read, update and delete category")
    void shouldPerformFullCrudOnCategory() throws Exception {
        // Create
        CategoryRequestDTO createRequest = new CategoryRequestDTO("Test Category");

        String response = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Category"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long categoryId = objectMapper.readTree(response).get("id").asLong();

        // Read
        mockMvc.perform(get("/api/categories/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Test Category"));

        // Update
        CategoryRequestDTO updateRequest = new CategoryRequestDTO("Updated Category");

        mockMvc.perform(put("/api/categories/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Updated Category"));

        // Delete
        mockMvc.perform(delete("/api/categories/" + categoryId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/categories/" + categoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Integration: Should prevent duplicate category names")
    void shouldPreventDuplicateCategoryNames() throws Exception {
        CategoryRequestDTO request = new CategoryRequestDTO("Duplicate Test");

        // Create first category
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Try to create duplicate
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Integration: Should validate category name requirements")
    void shouldValidateCategoryNameRequirements() throws Exception {
        // Empty name
        CategoryRequestDTO emptyName = new CategoryRequestDTO("");
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyName)))
                .andExpect(status().isBadRequest());

        // Name too short
        CategoryRequestDTO shortName = new CategoryRequestDTO("A");
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortName)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration: Should get all categories")
    void shouldGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}
