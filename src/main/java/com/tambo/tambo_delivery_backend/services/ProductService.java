package com.tambo.tambo_delivery_backend.services;

import com.tambo.tambo_delivery_backend.dto.request.CreateProductDtoAdmin;
import com.tambo.tambo_delivery_backend.dto.response.ProductDTO;
import com.tambo.tambo_delivery_backend.entities.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {

    public ProductDTO createProduct(CreateProductDtoAdmin dto);

    public List<ProductDTO> getAllProducts(UUID categoryId, UUID typeId, String name, BigDecimal minPrice,
            BigDecimal maxPrice, Boolean active, Boolean newArrival);

    ProductDTO getProductBySlug(String slug);

    ProductDTO getProductById(UUID id);

    Product getProductEntityById(UUID id);

    ProductDTO updateProduct(UUID id, CreateProductDtoAdmin productDto);

    boolean deleteProduct(UUID id);

}
