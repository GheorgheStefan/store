package com.air.practice.repository;

import com.air.practice.dto.ProductCategory;
import com.air.practice.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @Mock
    private ProductRepository productRepository;

    private Product testProduct;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        testProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(99.99f)
                .stock(10)
                .category(ProductCategory.ELECTRONICS)
                .build();
    }

    @Test
    void save_shouldPersistProduct() {
        when(productRepository.saveAndFlush(testProduct)).thenReturn(testProduct);
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        Product savedProduct = productRepository.saveAndFlush(testProduct);

        assertNotNull(savedProduct.getId());
        assertTrue(productRepository.findById(savedProduct.getId()).isPresent());
    }

    @Test
    void findByCategory_shouldReturnProductsInCategory() {
        Product product2 = Product.builder()
                .id(UUID.randomUUID())
                .name("Another Electronics")
                .description("Another Description")
                .price(49.99f)
                .stock(5)
                .category(ProductCategory.ELECTRONICS)
                .build();

        when(productRepository.findByCategory(ProductCategory.ELECTRONICS))
                .thenReturn(List.of(testProduct, product2));

        List<Product> results = productRepository.findByCategory(ProductCategory.ELECTRONICS);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> p.getCategory() == ProductCategory.ELECTRONICS));
    }

    @Test
    void findByNameContainingIgnoreCase_shouldReturnProductsMatchingName() {
        when(productRepository.findByNameContainingIgnoreCase("test"))
                .thenReturn(List.of(testProduct));

        List<Product> results = productRepository.findByNameContainingIgnoreCase("test");

        assertEquals(1, results.size());
        assertTrue(results.get(0).getName().toLowerCase().contains("test"));
    }

    @Test
    void findByNameContainingIgnoreCase_shouldBeCaseInsensitive() {
        when(productRepository.findByNameContainingIgnoreCase("TEST"))
                .thenReturn(List.of(testProduct));

        List<Product> results = productRepository.findByNameContainingIgnoreCase("TEST");

        assertEquals(1, results.size());
    }

    @Test
    void findByStockLessThan_shouldReturnLowStockProducts() {
        Product lowStockProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Low Stock Product")
                .description("Description")
                .price(29.99f)
                .stock(2)
                .category(ProductCategory.CLOTHING)
                .build();

        when(productRepository.findByStockLessThan(5))
                .thenReturn(List.of(lowStockProduct));

        List<Product> results = productRepository.findByStockLessThan(5);

        assertEquals(1, results.size());
        assertTrue(results.get(0).getStock() < 5);
    }

    @Test
    void existsByNameIgnoreCase_shouldReturnTrue_whenProductExists() {
        when(productRepository.existsByNameIgnoreCase("Test Product")).thenReturn(true);

        boolean exists = productRepository.existsByNameIgnoreCase("Test Product");

        assertTrue(exists);
    }

    @Test
    void existsByNameIgnoreCase_shouldReturnTrue_whenNameIsDifferentCase() {
        when(productRepository.existsByNameIgnoreCase("test product")).thenReturn(true);

        boolean exists = productRepository.existsByNameIgnoreCase("test product");

        assertTrue(exists);
    }

    @Test
    void existsByNameIgnoreCase_shouldReturnFalse_whenProductDoesNotExist() {
        when(productRepository.existsByNameIgnoreCase("Non Existent Product")).thenReturn(false);

        boolean exists = productRepository.existsByNameIgnoreCase("Non Existent Product");

        assertFalse(exists);
    }

    @Test
    void findById_shouldReturnProduct_whenExists() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productRepository.findById(productId);

        assertTrue(result.isPresent());
        assertEquals(testProduct.getName(), result.get().getName());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<Product> result = productRepository.findById(nonExistentId);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteById_shouldRemoveProduct() {
        productRepository.deleteById(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        Optional<Product> result = productRepository.findById(productId);

        assertFalse(result.isPresent());
    }
}
