package cue.edu.co.inventariopruebas.application.mapper;

import cue.edu.co.inventariopruebas.application.dto.ProductDTO;
import cue.edu.co.inventariopruebas.application.dto.ProductRequestDTO;
import cue.edu.co.inventariopruebas.domain.entity.Category;
import cue.edu.co.inventariopruebas.domain.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Mapper for Product entity and DTOs.
 */
@Component
public class ProductMapper {

    /**
     * Convert Product entity to ProductDTO.
     *
     * @param product the product entity
     * @return the product DTO
     */
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    /**
     * Convert ProductRequestDTO to Product entity.
     *
     * @param requestDTO the product request DTO
     * @param category   the category entity
     * @return the product entity
     */
    public Product toEntity(ProductRequestDTO requestDTO, Category category) {
        if (requestDTO == null) {
            return null;
        }
        return new Product(
                requestDTO.getName(),
                requestDTO.getDescription(),
                requestDTO.getPrice(),
                requestDTO.getStock(),
                category
        );
    }

    /**
     * Update an existing Product entity from ProductRequestDTO.
     *
     * @param product    the existing product entity
     * @param requestDTO the product request DTO
     * @param category   the category entity
     */
    public void updateEntityFromDTO(Product product, ProductRequestDTO requestDTO, Category category) {
        if (product != null && requestDTO != null) {
            product.setName(requestDTO.getName());
            product.setDescription(requestDTO.getDescription());
            product.setPrice(requestDTO.getPrice());
            product.setStock(requestDTO.getStock());
            if (category != null) {
                product.setCategory(category);
            }
        }
    }
}
