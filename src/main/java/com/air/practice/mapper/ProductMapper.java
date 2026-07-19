package com.air.practice.mapper;

import com.air.practice.dto.products.ProductCreateRequest;
import com.air.practice.dto.products.ProductResponse;
import com.air.practice.dto.products.ProductUpdateRequest;
import com.air.practice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductCreateRequest request);

    ProductResponse toResponse(Product product);

    void updateEntityFromRequest(
            ProductUpdateRequest request,
            @MappingTarget Product product
    );
}
