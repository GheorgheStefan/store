package com.air.practice.dto.products;

import com.air.practice.dto.ProductCategory;

public record ProductCreateRequest(
        String name,
        String description,
        Float price,
        Integer stock,
        ProductCategory category
) {
}
