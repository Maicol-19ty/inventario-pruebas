package cue.edu.co.inventariopruebas.infrastructure.controller;

import cue.edu.co.inventariopruebas.application.dto.CategoryDTO;
import cue.edu.co.inventariopruebas.application.dto.CategoryRequestDTO;
import cue.edu.co.inventariopruebas.application.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Category operations.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Get all categories.
     *
     * @return list of category DTOs
     */
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get a category by ID.
     *
     * @param id the category ID
     * @return the category DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Create a new category.
     *
     * @param requestDTO the category request DTO
     * @return the created category DTO
     */
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    /**
     * Update an existing category.
     *
     * @param id         the category ID
     * @param requestDTO the category request DTO
     * @return the updated category DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, requestDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Delete a category by ID.
     *
     * @param id the category ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
