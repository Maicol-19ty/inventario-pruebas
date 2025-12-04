package cue.edu.co.inventariopruebas.application.service;

import cue.edu.co.inventariopruebas.application.dto.ProductDTO;
import cue.edu.co.inventariopruebas.application.dto.ProductRequestDTO;
import cue.edu.co.inventariopruebas.application.mapper.ProductMapper;
import cue.edu.co.inventariopruebas.domain.entity.Category;
import cue.edu.co.inventariopruebas.domain.entity.Product;
import cue.edu.co.inventariopruebas.domain.exception.ResourceNotFoundException;
import cue.edu.co.inventariopruebas.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Product business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;

    /**
     * Get all products.
     *
     * @return list of product DTOs
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        log.debug("Getting all products");
        return productRepository.findAllWithCategory().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a product by ID.
     *
     * @param id the product ID
     * @return the product DTO
     * @throws ResourceNotFoundException if product not found
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        log.debug("Getting product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return productMapper.toDTO(product);
    }

    /**
     * Get products by category ID.
     *
     * @param categoryId the category ID
     * @return list of product DTOs
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        log.debug("Getting products for category id: {}", categoryId);
        return productRepository.findByCategoryId(categoryId).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search products by name.
     *
     * @param name the name to search for
     * @return list of matching product DTOs
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProductsByName(String name) {
        log.debug("Searching products with name containing: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new product.
     *
     * @param requestDTO the product request DTO
     * @return the created product DTO
     * @throws ResourceNotFoundException if category not found
     */
    @Transactional
    public ProductDTO createProduct(ProductRequestDTO requestDTO) {
        log.debug("Creating product with name: {}", requestDTO.getName());

        Category category = categoryService.getCategoryEntityById(requestDTO.getCategoryId());
        Product product = productMapper.toEntity(requestDTO, category);
        Product savedProduct = productRepository.save(product);
        log.info("Product created with id: {}", savedProduct.getId());
        return productMapper.toDTO(savedProduct);
    }

    /**
     * Update an existing product.
     *
     * @param id         the product ID
     * @param requestDTO the product request DTO
     * @return the updated product DTO
     * @throws ResourceNotFoundException if product or category not found
     */
    @Transactional
    public ProductDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        log.debug("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        Category category = categoryService.getCategoryEntityById(requestDTO.getCategoryId());
        productMapper.updateEntityFromDTO(product, requestDTO, category);
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated with id: {}", updatedProduct.getId());
        return productMapper.toDTO(updatedProduct);
    }

    /**
     * Delete a product by ID.
     *
     * @param id the product ID
     * @throws ResourceNotFoundException if product not found
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted with id: {}", id);
    }

    /**
     * Get products with low stock.
     *
     * @param threshold the stock threshold
     * @return list of product DTOs with stock below threshold
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        log.debug("Getting products with stock below: {}", threshold);
        return productRepository.findByStockLessThan(threshold).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }
}
