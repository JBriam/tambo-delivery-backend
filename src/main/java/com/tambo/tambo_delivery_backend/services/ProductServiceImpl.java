package com.tambo.tambo_delivery_backend.services;

import com.tambo.tambo_delivery_backend.dto.request.CreateProductDtoAdmin;
import com.tambo.tambo_delivery_backend.dto.response.ProductDTO;
import com.tambo.tambo_delivery_backend.entities.*;
import com.tambo.tambo_delivery_backend.exceptions.ResourceNotFoundEx;
import com.tambo.tambo_delivery_backend.mapper.ProductMapper;
import com.tambo.tambo_delivery_backend.repositories.BrandRepository;
import com.tambo.tambo_delivery_backend.repositories.CategoryRepository;
import com.tambo.tambo_delivery_backend.repositories.CategoryTypeRepository;
import com.tambo.tambo_delivery_backend.repositories.DiscountRepository;
import com.tambo.tambo_delivery_backend.repositories.ProductRepository;
import com.tambo.tambo_delivery_backend.specification.ProductSpecification;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;
        private final CategoryTypeRepository categoryTypeRepository;
        private final DiscountRepository discountRepository;
        private final BrandRepository brandRepository;

        // Obtener todos los productos por filtros
        @Override
        @Transactional(readOnly = true)
        public List<ProductDTO> getAllProducts(UUID categoryId, UUID typeId, String name, BigDecimal minPrice,
                        BigDecimal maxPrice, Boolean active, Boolean newArrival) {

                Specification<Product> spec = Specification.where(null);

                if (categoryId != null) {
                        spec = spec.and(ProductSpecification.hasCategoryId(categoryId));
                }
                if (typeId != null) {
                        spec = spec.and(ProductSpecification.hasCategoryTypeId(typeId));
                }
                if (StringUtils.isNotBlank(name)) {
                        spec = spec.and(ProductSpecification.hasNameLike(name));
                }
                if (minPrice != null) {
                        spec = spec.and(ProductSpecification.hasMinPrice(minPrice));
                }
                if (maxPrice != null) {
                        spec = spec.and(ProductSpecification.hasMaxPrice(maxPrice));
                }
                if (active != null) {
                        spec = spec.and(active ? ProductSpecification.isActive() : ProductSpecification.isInactive());
                }
                if (newArrival != null) {
                        spec = spec.and(newArrival ? ProductSpecification.isNewArrival()
                                        : ProductSpecification.isNotNewArrival());
                }

                return productRepository.findAll(spec).stream()
                                .map(ProductMapper::toDTO)
                                .collect(Collectors.toList());
        }

        // Agregar un nuevo producto
        @Override
        public ProductDTO createProduct(CreateProductDtoAdmin dto) {
                Category category = categoryRepository.findById(dto.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
                CategoryType categoryType = dto.getCategoryTypeId() != null
                                ? categoryTypeRepository.findById(dto.getCategoryTypeId())
                                                .orElseThrow(() -> new RuntimeException("Sub-categoria no encontrada"))
                                : null;
                Brand brand = brandRepository.findById(dto.getBrandId())
                                .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
                List<Discount> discounts = dto.getDiscountIds() != null
                                ? discountRepository.findAllById(dto.getDiscountIds())
                                : List.of();

                Product product = ProductMapper.toEntity(dto, category, categoryType, brand, discounts);
                Product saved = productRepository.save(product);
                return ProductMapper.toDTO(saved);
        }

        // Obtener todos los productos por categoria
        @Override
        @Transactional(readOnly = true)
        public ProductDTO getProductBySlug(String slug) {
                Product product = productRepository.findBySlug(slug)
                                .orElseThrow(() -> new RuntimeException("Product not found"));
                return ProductMapper.toDTO(product);
        }

        // Obtener un producto Dto por ID
        @Override
        @Transactional(readOnly = true)
        public ProductDTO getProductById(UUID id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundEx("Product Not Found!"));
                return ProductMapper.toDTO(product);
        }

        // Obtener un producto Dto por ID
        @Override
        @Transactional(readOnly = true)
        public Product getProductEntityById(UUID id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundEx("Product Not Found!"));
                return product;
        }

        @Override
        @Transactional
        public ProductDTO updateProduct(UUID id, CreateProductDtoAdmin dto) {
                Product existing = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

                // ========== ACTUALIZAR DATOS BÁSICOS ==========
                existing.setSlug(dto.getSlug());
                existing.setName(dto.getName());
                existing.setDescription(dto.getDescription());
                existing.setStock(dto.getStock());
                existing.setPrice(dto.getPrice());
                existing.setNewArrival(dto.getIsNewArrival());
                existing.setActive(dto.getIsActive());

                // ========== ACTUALIZAR BRAND ==========
                Brand brand = brandRepository.findById(dto.getBrandId())
                                .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
                existing.setBrand(brand);

                // ========== ACTUALIZAR CATEGORY ==========
                Category category = categoryRepository.findById(dto.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
                existing.setCategory(category);

                // ========== ACTUALIZAR CATEGORY TYPE ==========
                CategoryType categoryType = dto.getCategoryTypeId() != null
                                ? categoryTypeRepository.findById(dto.getCategoryTypeId()).orElse(null)
                                : null;
                existing.setCategoryType(categoryType);

                // ========== ACTUALIZAR RESOURCES ==========
                // Eliminar recursos antiguos (orphanRemoval = true se encarga)
                existing.getResources().clear();

                // Agregar nuevos recursos si existen
                if (dto.getResources() != null && !dto.getResources().isEmpty()) {
                        existing.getResources().addAll(
                                        dto.getResources().stream()
                                                        .map(rr -> Resources.builder()
                                                                        .name(rr.getName())
                                                                        .url(rr.getUrl())
                                                                        .isPrimary(rr.getIsPrimary())
                                                                        .type(rr.getType())
                                                                        .product(existing)
                                                                        .build())
                                                        .collect(Collectors.toList()));
                }

                // ========== ACTUALIZAR DISCOUNTS (ORDEN CORRECTO) ==========

                // PASO 1: Guardar referencia a los descuentos ANTIGUOS antes de modificar
                List<Discount> oldDiscounts = new ArrayList<>(existing.getDiscounts());

                // PASO 2: Remover este producto de los descuentos antiguos
                for (Discount oldDiscount : oldDiscounts) {
                        oldDiscount.getProducts().remove(existing);
                }

                // PASO 3: Buscar los nuevos descuentos
                List<Discount> newDiscounts = dto.getDiscountIds() != null && !dto.getDiscountIds().isEmpty()
                                ? discountRepository.findAllById(dto.getDiscountIds())
                                : new ArrayList<>();

                // PASO 4: Actualizar el lado del Product
                existing.setDiscounts(newDiscounts);

                // PASO 5: Añadir este producto a los nuevos descuentos (sincronización)
                for (Discount discount : newDiscounts) {
                        if (!discount.getProducts().contains(existing)) {
                                discount.getProducts().add(existing);
                        }
                }

                // ========== GUARDAR ==========
                Product updated = productRepository.save(existing);
                return ProductMapper.toDTO(updated);
        }

        @Override
        @Transactional
        public boolean deleteProduct(UUID id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

                try {
                        // Eliminar relaciones con descuentos primero
                        for (Discount discount : product.getDiscounts()) {
                                discount.getProducts().remove(product);
                        }
                        product.getDiscounts().clear();

                        // Eliminar recursos asociados (si existen)
                        if (product.getResources() != null) {
                                product.getResources().clear();
                        }

                        // Eliminar producto de la base de datos
                        productRepository.deleteById(id);
                        return true;

                } catch (Exception e) {
                        System.out.println("Error al eliminar el producto");
                        return false;
                }
        }

}
