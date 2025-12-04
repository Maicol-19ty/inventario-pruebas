package cue.edu.co.inventariopruebas.application.mapper;

import cue.edu.co.inventariopruebas.application.dto.CategoryDTO;
import cue.edu.co.inventariopruebas.application.dto.CategoryRequestDTO;
import cue.edu.co.inventariopruebas.domain.entity.Category;
import org.springframework.stereotype.Component;

/**
 * Mapper for Category entity and DTOs.
 */
@Component
public class CategoryMapper {

    /**
     * Convert Category entity to CategoryDTO.
     *
     * @param category the category entity
     * @return the category DTO
     */
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    /**
     * Convert CategoryRequestDTO to Category entity.
     *
     * @param requestDTO the category request DTO
     * @return the category entity
     */
    public Category toEntity(CategoryRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        return new Category(requestDTO.getName());
    }

    /**
     * Update an existing Category entity from CategoryRequestDTO.
     *
     * @param category   the existing category entity
     * @param requestDTO the category request DTO
     */
    public void updateEntityFromDTO(Category category, CategoryRequestDTO requestDTO) {
        if (category != null && requestDTO != null) {
            category.setName(requestDTO.getName());
        }
    }
}
