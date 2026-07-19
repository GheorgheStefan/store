package com.air.practice.repository;

import com.air.practice.dto.ProductCategory;
import com.air.practice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByCategory(ProductCategory category);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByStockLessThan(Integer stock);

    boolean existsByNameIgnoreCase(String name);
}
