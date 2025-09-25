package com.tambo.tambo_delivery_backend.specification;

import com.tambo.tambo_delivery_backend.entities.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductSpecification {

    public static Specification<Product> hasCategoryId(UUID categorId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category").get("id"), categorId);
    }

    public static Specification<Product> hasCategoryTypeId(UUID typeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("categoryType").get("id"), typeId);
    }

    public static Specification<Product> hasNameLike(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasMinPrice(BigDecimal minPrice) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("isActive"));
    }

    public static Specification<Product> isInactive() {
        return (root, query, cb) -> cb.isFalse(root.get("isActive"));
    }

    public static Specification<Product> isNewArrival() {
        return (root, query, cb) -> cb.isTrue(root.get("isNewArrival"));
    }

    public static Specification<Product> isNotNewArrival() {
        return (root, query, cb) -> cb.isFalse(root.get("isNewArrival"));
    }

}
