package cue.edu.co.inventariopruebas.application.service;

import cue.edu.co.inventariopruebas.application.dto.CategoryDTO;
import cue.edu.co.inventariopruebas.application.dto.CategoryRequestDTO;
import cue.edu.co.inventariopruebas.application.mapper.CategoryMapper;
import cue.edu.co.inventariopruebas.domain.entity.Category;
import cue.edu.co.inventariopruebas.domain.exception.DuplicateResourceException;
import cue.edu.co.inventariopruebas.domain.exception.ResourceNotFoundException;
import cue.edu.co.inventariopruebas.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Category business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Get all categories.
     *
     * @return list of category DTOs
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        log.debug("Getting all categories");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a category by ID.
     *
     * @param id the category ID
     * @return the category DTO
     * @throws ResourceNotFoundException if category not found
     */
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        log.debug("Getting category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return categoryMapper.toDTO(category);
    }

    /**
     * Create a new category.
     *
     * @param requestDTO the category request DTO
     * @return the created category DTO
     * @throws DuplicateResourceException if category name already exists
     */
    @Transactional
    public CategoryDTO createCategory(CategoryRequestDTO requestDTO) {
        log.debug("Creating category with name: {}", requestDTO.getName());

        if (categoryRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateResourceException("Category", "name", requestDTO.getName());
        }

        Category category = categoryMapper.toEntity(requestDTO);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created with id: {}", savedCategory.getId());
        return categoryMapper.toDTO(savedCategory);
    }

    /**
     * Update an existing category.
     *
     * @param id         the category ID
     * @param requestDTO the category request DTO
     * @return the updated category DTO
     * @throws ResourceNotFoundException  if category not found
     * @throws DuplicateResourceException if category name already exists
     */
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryRequestDTO requestDTO) {
        log.debug("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        if (!category.getName().equals(requestDTO.getName()) &&
                categoryRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateResourceException("Category", "name", requestDTO.getName());
        }

        categoryMapper.updateEntityFromDTO(category, requestDTO);
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated with id: {}", updatedCategory.getId());
        return categoryMapper.toDTO(updatedCategory);
    }

    /**
     * Delete a category by ID.
     *
     * @param id the category ID
     * @throws ResourceNotFoundException if category not found
     */
    @Transactional
    public void deleteCategory(Long id) {
        log.debug("Deleting category with id: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }
        categoryRepository.deleteById(id);
        log.info("Category deleted with id: {}", id);
    }

    /**
     * Get a Category entity by ID (for internal use).
     *
     * @param id the category ID
     * @return the category entity
     * @throws ResourceNotFoundException if category not found
     */
    @Transactional(readOnly = true)
    public Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }
}
