package cue.edu.co.inventariopruebas.application.service;

import cue.edu.co.inventariopruebas.application.dto.ProductDTO;
import cue.edu.co.inventariopruebas.application.dto.ProductRequestDTO;
import cue.edu.co.inventariopruebas.application.mapper.ProductMapper;
import cue.edu.co.inventariopruebas.domain.entity.Category;
import cue.edu.co.inventariopruebas.domain.entity.Product;
import cue.edu.co.inventariopruebas.domain.exception.ResourceNotFoundException;
import cue.edu.co.inventariopruebas.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;
    private ProductRequestDTO productRequestDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("High performance laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setStock(10);
        product.setCategory(category);

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
    @DisplayName("Should get all products successfully")
    void shouldGetAllProducts() {
        when(productRepository.findAllWithCategory()).thenReturn(Arrays.asList(product));
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        List<ProductDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAllWithCategory();
    }

    @Test
    @DisplayName("Should get product by ID successfully")
    void shouldGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProduct() {
        when(categoryService.getCategoryEntityById(1L)).thenReturn(category);
        when(productMapper.toEntity(productRequestDTO, category)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(productRequestDTO);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(categoryService, times(1)).getCategoryEntityById(1L);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryService.getCategoryEntityById(1L)).thenReturn(category);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.updateProduct(1L, productRequestDTO);

        assertNotNull(result);
        verify(productMapper, times(1)).updateEntityFromDTO(product, productRequestDTO, category);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent product")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should get products by category ID")
    void shouldGetProductsByCategoryId() {
        when(productRepository.findByCategoryId(1L)).thenReturn(Arrays.asList(product));
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        List<ProductDTO> result = productService.getProductsByCategoryId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByCategoryId(1L);
    }

    @Test
    @DisplayName("Should search products by name")
    void shouldSearchProductsByName() {
        when(productRepository.findByNameContainingIgnoreCase("Laptop")).thenReturn(Arrays.asList(product));
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        List<ProductDTO> result = productService.searchProductsByName("Laptop");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("Laptop");
    }

    @Test
    @DisplayName("Should get low stock products")
    void shouldGetLowStockProducts() {
        when(productRepository.findByStockLessThan(10)).thenReturn(Arrays.asList(product));
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        List<ProductDTO> result = productService.getLowStockProducts(10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByStockLessThan(10);
    }
}
