package cue.edu.co.inventariopruebas.infrastructure.controller;

import cue.edu.co.inventariopruebas.application.dto.ProductDTO;
import cue.edu.co.inventariopruebas.application.dto.ProductRequestDTO;
import cue.edu.co.inventariopruebas.application.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Product operations.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    /**
     * Get all products.
     *
     * @return list of product DTOs
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {

        List<ProductDTO> products;

        if (categoryId != null) {
            products = productService.getProductsByCategoryId(categoryId);
        } else if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProductsByName(search);
        } else {
            products = productService.getAllProducts();
        }

        return ResponseEntity.ok(products);
    }

    /**
     * Get a product by ID.
     *
     * @param id the product ID
     * @return the product DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Get products with low stock.
     *
     * @param threshold the stock threshold
     * @return list of products with low stock
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<ProductDTO> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    /**
     * Create a new product.
     *
     * @param requestDTO the product request DTO
     * @return the created product DTO
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductDTO createdProduct = productService.createProduct(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Update an existing product.
     *
     * @param id         the product ID
     * @param requestDTO the product request DTO
     * @return the updated product DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, requestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Delete a product by ID.
     *
     * @param id the product ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
