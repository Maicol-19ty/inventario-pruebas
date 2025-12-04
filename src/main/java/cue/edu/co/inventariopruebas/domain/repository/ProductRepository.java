package cue.edu.co.inventariopruebas.domain.repository;

import cue.edu.co.inventariopruebas.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products by category ID.
     *
     * @param categoryId the category ID
     * @return list of products in the category
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Find products by name containing the given string (case-insensitive).
     *
     * @param name the name to search for
     * @return list of matching products
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find all products with stock below a given threshold.
     *
     * @param threshold the stock threshold
     * @return list of products with low stock
     */
    List<Product> findByStockLessThan(Integer threshold);

    /**
     * Find all products with their categories (eager loading).
     *
     * @return list of all products with categories loaded
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category")
    List<Product> findAllWithCategory();
}
