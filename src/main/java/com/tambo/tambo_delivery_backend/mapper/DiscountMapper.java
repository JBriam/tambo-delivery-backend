package com.tambo.tambo_delivery_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tambo.tambo_delivery_backend.dto.request.DiscountRequestDTO;
import com.tambo.tambo_delivery_backend.dto.response.DiscountDTO;
import com.tambo.tambo_delivery_backend.dto.response.ProductDiscountDTO;
import com.tambo.tambo_delivery_backend.entities.Discount;
import com.tambo.tambo_delivery_backend.entities.Product;
import com.tambo.tambo_delivery_backend.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DiscountMapper {

    private final ProductRepository productRepository;

    public Discount toEntity(DiscountRequestDTO request) {
        List<Product> products = request.getProductIds() != null
                ? productRepository.findAllById(request.getProductIds())
                : List.of();

        return Discount.builder()
                .name(request.getName())
                .percentage(request.getPercentage())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(request.getIsActive())
                .products(products)
                .build();
    }

    public DiscountDTO toDTO(Discount discount) {
        List<ProductDiscountDTO> productSummaries = discount.getProducts().stream()
                .map(product -> ProductDiscountDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .build())
                .collect(Collectors.toList());

        return DiscountDTO.builder()
                .id(discount.getId())
                .name(discount.getName())
                .percentage(discount.getPercentage())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .isActive(discount.getIsActive())
                .products(productSummaries)
                .build();
    }
}
