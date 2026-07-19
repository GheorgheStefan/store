package com.air.practice.dto.products;

import com.air.practice.dto.ProductCategory;

import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        Float price,
        Integer stock,
        ProductCategory category
) {
}
