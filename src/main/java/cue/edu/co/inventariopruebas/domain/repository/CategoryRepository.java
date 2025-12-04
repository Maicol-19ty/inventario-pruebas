package cue.edu.co.inventariopruebas.domain.repository;

import cue.edu.co.inventariopruebas.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find a category by its name.
     *
     * @param name the category name
     * @return an Optional containing the category if found
     */
    Optional<Category> findByName(String name);

    /**
     * Check if a category with the given name exists.
     *
     * @param name the category name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);
}
