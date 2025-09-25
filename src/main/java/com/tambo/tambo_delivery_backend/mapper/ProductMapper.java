package com.tambo.tambo_delivery_backend.mapper;

import com.tambo.tambo_delivery_backend.dto.BrandDTO;
import com.tambo.tambo_delivery_backend.dto.DiscountDTO;
import com.tambo.tambo_delivery_backend.dto.ProductDTO;
import com.tambo.tambo_delivery_backend.dto.CreateProductDtoAdmin;
import com.tambo.tambo_delivery_backend.dto.ResourceDTO;
import com.tambo.tambo_delivery_backend.entities.*;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

        public static ProductDTO toDTO(Product product) {
                return ProductDTO.builder()
                                .id(product.getId())
                                .thumbnail(null != product.getResources() && !product.getResources().isEmpty()
                                                ? getProductThumbnail(product.getResources())
                                                : null)
                                .slug(product.getSlug())
                                .name(product.getName())
                                .description(product.getDescription())
                                .price(product.getPrice())
                                .discountPercentage(getPercentageDiscount(product))
                                .discountedPrice(calculateDiscountedPrice(product))
                                .stock(product.getStock())
                                .brand(BrandDTO.builder()
                                                .id(product.getBrand().getId())
                                                .name(product.getBrand().getName())
                                                .build())
                                .rating(product.getRating())
                                .isNewArrival(product.isNewArrival())
                                .isActive(product.isActive())
                                .createdAt(product.getCreatedAt())
                                .updatedAt(product.getUpdatedAt())
                                .category(CategoryMapper.toDTO(product.getCategory()))
                                .categoryType(
                                                product.getCategoryType() != null
                                                                ? CategoryTypeMapper.toDTO(product.getCategoryType())
                                                                : null)
                                .resources(product.getResources().stream()
                                                .map(r -> ResourceDTO.builder()
                                                                .id(r.getId())
                                                                .name(r.getName())
                                                                .url(r.getUrl())
                                                                .isPrimary(r.getIsPrimary())
                                                                .type(r.getType())
                                                                .build())
                                                .collect(Collectors.toList()))
                                .discounts(product.getDiscounts().stream()
                                                .map(d -> DiscountDTO.builder()
                                                                .id(d.getId())
                                                                .name(d.getName())
                                                                .percentage(d.getPercentage())
                                                                .build())
                                                .collect(Collectors.toList()))
                                .build();
        }

        public static Product toEntity(CreateProductDtoAdmin dto, Category category, CategoryType categoryType,
                        Brand brand,
                        List<Discount> discounts) {
                Product product = Product.builder()
                                .slug(dto.getSlug())
                                .name(dto.getName())
                                .description(dto.getDescription())
                                .price(dto.getPrice())
                                .stock(dto.getStock())
                                .brand(brand)
                                .isNewArrival(dto.getIsNewArrival())
                                .isActive(dto.getIsActive())
                                .category(category)
                                .categoryType(categoryType)
                                .discounts(discounts)
                                .build();

                var resources = dto.getResources().stream()
                                .map(rr -> Resources.builder()
                                                .name(rr.getName())
                                                .url(rr.getUrl())
                                                .isPrimary(rr.getIsPrimary())
                                                .type(rr.getType())
                                                .product(product)
                                                .build())
                                .collect(Collectors.toList());
                product.setResources(resources);

                return product;
        }

        // Obtener la miniatura del producto
        private static String getProductThumbnail(List<Resources> resources) {
                return resources.stream().filter(Resources::getIsPrimary).findFirst().map(Resources::getUrl)
                                .orElse(null);
        }

        // Calcular el precio con descuento
        private static BigDecimal calculateDiscountedPrice(Product product) {
                BigDecimal originalPrice = product.getPrice();
                Discount activeDiscount = product.getDiscounts().stream()
                                .filter(d -> d.getIsActive() && d.getStartDate().isBefore(LocalDateTime.now())
                                                &&
                                                d.getEndDate().isAfter(LocalDateTime.now()))
                                .findFirst()
                                .orElse(null);

                if (activeDiscount == null) {
                        return originalPrice;
                }

                if (activeDiscount.getPercentage() != null) {
                        return originalPrice
                                        .subtract(originalPrice.multiply(activeDiscount.getPercentage())
                                                        .divide(BigDecimal.valueOf(100)));
                } else {
                        return originalPrice;
                }
        }

        // Obtener el porcentaje de descuento
        private static BigDecimal getPercentageDiscount(Product product) {
                Discount activeDiscount = product.getDiscounts().stream()
                                .filter(d -> d.getIsActive() && d.getStartDate().isBefore(LocalDateTime.now())
                                                &&
                                                d.getEndDate().isAfter(LocalDateTime.now()))
                                .findFirst()
                                .orElse(null);

                if (activeDiscount == null) {
                        return BigDecimal.ZERO;
                }

                return activeDiscount.getPercentage();
        }
}
