package cue.edu.co.inventariopruebas.application.service;

import cue.edu.co.inventariopruebas.application.dto.CategoryDTO;
import cue.edu.co.inventariopruebas.application.dto.CategoryRequestDTO;
import cue.edu.co.inventariopruebas.application.mapper.CategoryMapper;
import cue.edu.co.inventariopruebas.domain.entity.Category;
import cue.edu.co.inventariopruebas.domain.exception.DuplicateResourceException;
import cue.edu.co.inventariopruebas.domain.exception.ResourceNotFoundException;
import cue.edu.co.inventariopruebas.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;
    private CategoryRequestDTO categoryRequestDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Electronics");

        categoryRequestDTO = new CategoryRequestDTO("Electronics");
    }

    @Test
    @DisplayName("Should get all categories successfully")
    void shouldGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));
        when(categoryMapper.toDTO(any(Category.class))).thenReturn(categoryDTO);

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get category by ID successfully")
    void shouldGetCategoryById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when category not found")
    void shouldThrowExceptionWhenCategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategory() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryMapper.toEntity(categoryRequestDTO)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.createCategory(categoryRequestDTO);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).existsByName("Electronics");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when category name exists")
    void shouldThrowExceptionWhenCategoryNameExists() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(categoryRequestDTO));
        verify(categoryRepository, times(1)).existsByName("Electronics");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update category successfully")
    void shouldUpdateCategory() {
        CategoryRequestDTO updateRequest = new CategoryRequestDTO("Updated Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Updated Electronics")).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.updateCategory(1L, updateRequest);

        assertNotNull(result);
        verify(categoryMapper, times(1)).updateEntityFromDTO(category, updateRequest);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Should delete category successfully")
    void shouldDeleteCategory() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent category")
    void shouldThrowExceptionWhenDeletingNonExistentCategory() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, never()).deleteById(any());
    }
}
