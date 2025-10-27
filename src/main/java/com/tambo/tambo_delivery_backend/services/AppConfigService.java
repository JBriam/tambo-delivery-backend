package com.tambo.tambo_delivery_backend.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tambo.tambo_delivery_backend.dto.response.CategoryButtonDTO;
import com.tambo.tambo_delivery_backend.dto.response.ProductSectionDTO;
import com.tambo.tambo_delivery_backend.dto.response.SliderImageDTO;
import com.tambo.tambo_delivery_backend.entities.Category;
import com.tambo.tambo_delivery_backend.entities.CategoryButton;
import com.tambo.tambo_delivery_backend.entities.ProductSection;
import com.tambo.tambo_delivery_backend.entities.SliderImage;
import com.tambo.tambo_delivery_backend.mapper.CategoryButtonMapper;
import com.tambo.tambo_delivery_backend.mapper.ProductSectionMapper;
import com.tambo.tambo_delivery_backend.mapper.SliderImageMapper;
import com.tambo.tambo_delivery_backend.repositories.CategoryButtonRepository;
import com.tambo.tambo_delivery_backend.repositories.CategoryRepository;
import com.tambo.tambo_delivery_backend.repositories.ProductSectionRepository;
import com.tambo.tambo_delivery_backend.repositories.SliderImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppConfigService {

    private final SliderImageRepository sliderRepo;
    private final CategoryButtonRepository buttonRepo;
    private final ProductSectionRepository sectionRepo;
    private final CategoryRepository categoryRepository;

    // --- Slider
    public List<SliderImageDTO> getSliderImages() {
        return sliderRepo.findAllByOrderByPositionAsc().stream()
                .map(SliderImageMapper::toDTO).toList();
    }

    public SliderImageDTO saveSlider(SliderImageDTO dto) {
        SliderImage image = SliderImageMapper.toEntity(dto);
        return SliderImageMapper.toDTO(sliderRepo.save(image));
    }

    public void deleteSlider(UUID id) {
        sliderRepo.deleteById(id);
    }

    // --- Botones de categoría
    public List<CategoryButtonDTO> getCategoryButtons() {
        return buttonRepo.findAllByOrderByPositionAsc().stream()
                .map(CategoryButtonMapper::toDTO).toList();
    }

    public CategoryButtonDTO saveCategoryButton(CategoryButtonDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        CategoryButton btn = new CategoryButton();
        btn.setCategory(category);
        btn.setPosition(dto.getPosition());
        return CategoryButtonMapper.toDTO(buttonRepo.save(btn));
    }

    public void deleteCategoryButton(UUID id) {
        buttonRepo.deleteById(id);
    }

    // --- Secciones de productos
    public List<ProductSectionDTO> getProductSections() {
        return sectionRepo.findAllByOrderByPositionAsc().stream()
                .map(ProductSectionMapper::toDTO).toList();
    }

    public ProductSectionDTO saveProductSection(ProductSectionDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        ProductSection section = new ProductSection();
        section.setCategory(category);
        section.setMaxProducts(dto.getMaxProducts() != null ? dto.getMaxProducts() : 8);
        section.setPosition(dto.getPosition());
        return ProductSectionMapper.toDTO(sectionRepo.save(section));
    }

    public void updateProductSection(UUID id, ProductSectionDTO dto) {
        ProductSection existing = sectionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product section not found"));

        if (null != existing) {
            ProductSection update = ProductSectionMapper.toEntity(dto);
            sectionRepo.save(update);
        }

    }

    public void deleteProductSection(UUID id) {
        sectionRepo.deleteById(id);
    }
}
