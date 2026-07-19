package com.air.practice.service;

import com.air.practice.dto.products.ProductCreateRequest;
import com.air.practice.dto.products.ProductResponse;
import com.air.practice.dto.products.ProductStockUpdateRequest;
import com.air.practice.dto.products.ProductUpdateRequest;
import com.air.practice.mapper.ProductMapper;
import com.air.practice.repository.ProductRepository;
import com.air.sec.config.exceptions.ProductNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest productCreateRequest) {
        log.info("Creating product: {}", productCreateRequest.name());

        var product = productMapper.toEntity(productCreateRequest);
        var savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Transactional
    public List<ProductResponse> getProducts() {
        log.info("Fetching all products");

        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional
    public ProductResponse getProduct(UUID productId) {
        log.info("Fetching product with ID: {}", productId);

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, ProductUpdateRequest productUpdateRequest) {
        log.info("Updating product with ID: {}", productId);

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productMapper.updateEntityFromRequest(productUpdateRequest, product);
        var updatedProduct = productRepository.save(product);

        log.info("Product with ID: {} updated successfully", productId);

        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public ProductResponse updateStock(UUID productId, @Valid ProductStockUpdateRequest request) {
        log.info("Updating stock for product ID: {} to {}", productId, request.stock());

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        product.setStock(request.stock());
        var updatedProduct = productRepository.save(product);

        log.info("Stock for product ID: {} updated successfully to {}", productId, request.stock());
        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        log.info("Deleting product with ID: {}", productId);

        productRepository.deleteById(productId);
        
        log.info("Product with ID: {} deleted successfully", productId);
    }
}
