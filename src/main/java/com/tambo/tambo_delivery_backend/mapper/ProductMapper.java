package com.tambo.tambo_delivery_backend.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tambo.tambo_delivery_backend.dto.request.CreateProductDtoAdmin;
import com.tambo.tambo_delivery_backend.dto.response.BrandDTO;
import com.tambo.tambo_delivery_backend.dto.response.DiscountDTO;
import com.tambo.tambo_delivery_backend.dto.response.ProductDTO;
import com.tambo.tambo_delivery_backend.dto.response.ResourceDTO;
import com.tambo.tambo_delivery_backend.entities.Brand;
import com.tambo.tambo_delivery_backend.entities.Category;
import com.tambo.tambo_delivery_backend.entities.CategoryType;
import com.tambo.tambo_delivery_backend.entities.Discount;
import com.tambo.tambo_delivery_backend.entities.Product;
import com.tambo.tambo_delivery_backend.entities.Resources;

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
                                                .description(product.getBrand().getDescription())
                                                .imageUrl(product.getBrand().getImageUrl())
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
                                                                .startDate(d.getStartDate())
                                                                .endDate(d.getEndDate())
                                                                .isActive(d.getIsActive())
                                                                .products(null) // null para evitar referencia circular
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
                                .filter(d -> d.getIsActive() != null && d.getIsActive() 
                                                && d.getStartDate() != null && !d.getStartDate().isAfter(LocalDate.now())
                                                && d.getEndDate() != null && !d.getEndDate().isBefore(LocalDate.now()))
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
                                .filter(d -> d.getIsActive() != null && d.getIsActive() 
                                                && d.getStartDate() != null && !d.getStartDate().isAfter(LocalDate.now())
                                                && d.getEndDate() != null && !d.getEndDate().isBefore(LocalDate.now()))
                                .findFirst()
                                .orElse(null);

                if (activeDiscount == null) {
                        return BigDecimal.ZERO;
                }

                return activeDiscount.getPercentage();
        }
}
