package com.air.practice.service;

import com.air.practice.dto.ProductCategory;
import com.air.practice.dto.products.ProductCreateRequest;
import com.air.practice.dto.products.ProductResponse;
import com.air.practice.dto.products.ProductStockUpdateRequest;
import com.air.practice.dto.products.ProductUpdateRequest;
import com.air.practice.entity.Product;
import com.air.practice.mapper.ProductMapper;
import com.air.practice.repository.ProductRepository;
import com.air.sec.config.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(99.99f)
                .stock(10)
                .category(ProductCategory.ELECTRONICS)
                .build();
    }

    @Test
    void createProduct_shouldReturnProductResponse_whenProductIsValid() {
        var request = new ProductCreateRequest("New Product", "Description", 49.99f, 5, ProductCategory.ELECTRONICS);
        var response = new ProductResponse(productId, "New Product", "Description", 49.99f, 5, ProductCategory.ELECTRONICS);

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertEquals(response, result);
        verify(productMapper).toEntity(request);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(product);
    }

    @Test
    void getProducts_shouldReturnListOfProducts() {
        var product2 = Product.builder()
                .id(UUID.randomUUID())
                .name("Product 2")
                .description("Description 2")
                .price(29.99f)
                .stock(20)
                .category(ProductCategory.CLOTHING)
                .build();

        var response1 = new ProductResponse(product.getId(), "Test Product", "Test Description", 99.99f, 10, ProductCategory.ELECTRONICS);
        var response2 = new ProductResponse(product2.getId(), "Product 2", "Description 2", 29.99f, 20, ProductCategory.CLOTHING);

        when(productRepository.findAll()).thenReturn(List.of(product, product2));
        when(productMapper.toResponse(product)).thenReturn(response1);
        when(productMapper.toResponse(product2)).thenReturn(response2);

        List<ProductResponse> results = productService.getProducts();

        assertEquals(List.of(response1, response2), results);
        verify(productRepository).findAll();
        verify(productMapper).toResponse(product);
        verify(productMapper).toResponse(product2);
    }

    @Test
    void getProduct_shouldReturnProductResponse_whenProductExists() {
        var response = new ProductResponse(productId, "Test Product", "Test Description", 99.99f, 10, ProductCategory.ELECTRONICS);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.getProduct(productId);

        assertEquals(response, result);
        verify(productRepository).findById(productId);
        verify(productMapper).toResponse(product);
    }

    @Test
    void getProduct_shouldThrowWhenProductNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(productId));

        verify(productRepository).findById(productId);
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    void updateProduct_shouldReturnUpdatedProductResponse_whenProductExists() {
        var updateRequest = new ProductUpdateRequest("Updated Name", "Updated Description", 79.99f, 15, ProductCategory.OTHER);
        var updatedProduct = Product.builder()
                .id(productId)
                .name("Updated Name")
                .description("Updated Description")
                .price(79.99f)
                .stock(15)
                .category(ProductCategory.OTHER)
                .build();
        var response = new ProductResponse(productId, "Updated Name", "Updated Description", 79.99f, 15, ProductCategory.OTHER);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(updatedProduct);
        when(productMapper.toResponse(updatedProduct)).thenReturn(response);

        ProductResponse result = productService.updateProduct(productId, updateRequest);

        assertEquals(response, result);
        verify(productRepository).findById(productId);
        verify(productMapper).updateEntityFromRequest(updateRequest, product);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(updatedProduct);
    }

    @Test
    void updateProduct_shouldThrowWhenProductNotFound() {
        var updateRequest = new ProductUpdateRequest("Updated Name", "Updated Description", 79.99f, 15, ProductCategory.OTHER);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(productId, updateRequest));

        verify(productRepository).findById(productId);
        verify(productMapper, never()).updateEntityFromRequest(any(), any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateStock_shouldReturnProductWithUpdatedStock_whenProductExists() {
        var stockRequest = new ProductStockUpdateRequest(50);
        var updatedProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(99.99f)
                .stock(50)
                .category(ProductCategory.ELECTRONICS)
                .build();
        var response = new ProductResponse(productId, "Test Product", "Test Description", 99.99f, 50, ProductCategory.ELECTRONICS);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(updatedProduct);
        when(productMapper.toResponse(updatedProduct)).thenReturn(response);

        ProductResponse result = productService.updateStock(productId, stockRequest);

        assertEquals(response, result);
        assertEquals(50, product.getStock());
        verify(productRepository).findById(productId);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(updatedProduct);
    }

    @Test
    void updateStock_shouldThrowWhenProductNotFound() {
        var stockRequest = new ProductStockUpdateRequest(50);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateStock(productId, stockRequest));

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_shouldDeleteProduct() {
        productService.deleteProduct(productId);

        verify(productRepository).deleteById(productId);
    }
}
