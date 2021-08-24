package com.somecompany.warehouse.repository;

import com.somecompany.warehouse.model.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Default ProductRepository
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

  /**
   * Find Optional product by name
   *
   * @param name productName
   */
  Optional<Product> findByName(String name);
}
