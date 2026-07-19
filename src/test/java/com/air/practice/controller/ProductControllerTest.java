package com.air.practice.controller;

import com.air.practice.dto.ProductCategory;
import com.air.practice.dto.products.ProductCreateRequest;
import com.air.practice.dto.products.ProductResponse;
import com.air.practice.dto.products.ProductStockUpdateRequest;
import com.air.practice.dto.products.ProductUpdateRequest;
import com.air.practice.service.ProductService;
import com.air.sec.config.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductController(productService))
                .build();
    }

    private Authentication createMockAuthentication() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        return auth;
    }

    @Test
    void createProduct_shouldReturnCreatedProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductResponse response = new ProductResponse(
                productId,
                "Test Product",
                "Test Description",
                99.99f,
                10,
                ProductCategory.ELECTRONICS
        );

        when(productService.createProduct(any(ProductCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Test Product",
                                  "description": "Test Description",
                                  "price": 99.99,
                                  "stock": 10,
                                  "category": "ELECTRONICS"
                                }
                                """)
                        .principal(createMockAuthentication()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.category").value("ELECTRONICS"));

        verify(productService).createProduct(any(ProductCreateRequest.class));
    }

    @Test
    void getProducts_shouldReturnAllProducts() throws Exception {
        ProductResponse product1 = new ProductResponse(UUID.randomUUID(), "Product 1", "Description 1", 49.99f, 5, ProductCategory.CLOTHING);
        ProductResponse product2 = new ProductResponse(UUID.randomUUID(), "Product 2", "Description 2", 29.99f, 20, ProductCategory.OTHER);

        when(productService.getProducts()).thenReturn(List.of(product1, product2));

        mockMvc.perform(get("/products")
                        .principal(createMockAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].category").value("CLOTHING"))
                .andExpect(jsonPath("$[1].name").value("Product 2"))
                .andExpect(jsonPath("$[1].category").value("OTHER"));

        verify(productService).getProducts();
    }

    @Test
    void getProduct_shouldReturnProduct_whenProductExists() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductResponse response = new ProductResponse(
                productId,
                "Test Product",
                "Test Description",
                99.99f,
                10,
                ProductCategory.ELECTRONICS
        );

        when(productService.getProduct(productId)).thenReturn(response);

        mockMvc.perform(get("/products/{productId}", productId)
                        .principal(createMockAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.stock").value(10));

        verify(productService).getProduct(productId);
    }

    @Test
    void getProduct_shouldReturnNotFound_whenProductNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        when(productService.getProduct(productId)).thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(get("/products/{productId}", productId)
                        .principal(createMockAuthentication()))
                .andExpect(status().isNotFound());

        verify(productService).getProduct(productId);
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductResponse response = new ProductResponse(
                productId,
                "Updated Product",
                "Updated Description",
                79.99f,
                15,
                ProductCategory.OTHER
        );

        when(productService.updateProduct(eq(productId), any(ProductUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Product",
                                  "description": "Updated Description",
                                  "price": 79.99,
                                  "stock": 15,
                                  "category": "OTHER"
                                }
                                """)
                        .principal(createMockAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.price").value(79.99))
                .andExpect(jsonPath("$.stock").value(15))
                .andExpect(jsonPath("$.category").value("OTHER"));

        verify(productService).updateProduct(eq(productId), any(ProductUpdateRequest.class));
    }

    @Test
    void updateProduct_shouldReturnNotFound_whenProductNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        when(productService.updateProduct(eq(productId), any(ProductUpdateRequest.class)))
                .thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(put("/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Product",
                                  "description": "Updated Description",
                                  "price": 79.99,
                                  "stock": 15,
                                  "category": "OTHER"
                                }
                                """)
                        .principal(createMockAuthentication()))
                .andExpect(status().isNotFound());

        verify(productService).updateProduct(eq(productId), any(ProductUpdateRequest.class));
    }

    @Test
    void updateStock_shouldReturnProductWithUpdatedStock() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductResponse response = new ProductResponse(
                productId,
                "Test Product",
                "Test Description",
                99.99f,
                50,
                ProductCategory.ELECTRONICS
        );

        when(productService.updateStock(eq(productId), any(ProductStockUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/products/{productId}/stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "stock": 50
                                }
                                """)
                        .principal(createMockAuthentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(50));

        verify(productService).updateStock(eq(productId), any(ProductStockUpdateRequest.class));
    }

    @Test
    void updateStock_shouldReturnNotFound_whenProductNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        when(productService.updateStock(eq(productId), any(ProductStockUpdateRequest.class)))
                .thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(patch("/products/{productId}/stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "stock": 50
                                }
                                """)
                        .principal(createMockAuthentication()))
                .andExpect(status().isNotFound());

        verify(productService).updateStock(eq(productId), any(ProductStockUpdateRequest.class));
    }

    @Test
    void deleteProduct_shouldReturnNoContent() throws Exception {
        UUID productId = UUID.randomUUID();

        mockMvc.perform(delete("/products/{productId}", productId)
                        .principal(createMockAuthentication()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(productService).deleteProduct(productId);
    }

    @Test
    void createProduct_shouldReturnBadRequest_whenRequestBodyIsInvalidJson() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json}")
                        .principal(createMockAuthentication()))
                .andExpect(status().isBadRequest());
    }
}
