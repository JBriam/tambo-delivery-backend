package com.tambo.tambo_delivery_backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tambo.tambo_delivery_backend.dto.CategoryTypeDTO;
import com.tambo.tambo_delivery_backend.dto.CategoryTypeRequestDTO;
import com.tambo.tambo_delivery_backend.entities.Category;
import com.tambo.tambo_delivery_backend.entities.CategoryType;
import com.tambo.tambo_delivery_backend.repositories.CategoryRepository;
import com.tambo.tambo_delivery_backend.repositories.CategoryTypeRepositoty;

@ExtendWith(MockitoExtension.class)
public class CategoryTypeServiceTest {

    @Mock
    private CategoryTypeRepositoty categoryTypeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryTypeService categoryTypeService;

    private Category category;
    private CategoryType categoryType;
    private CategoryTypeRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(UUID.randomUUID())
                .name("Electronics")
                .code("ELECT")
                .description("Electronic devices")
                .build();

        categoryType = CategoryType.builder()
                .id(UUID.randomUUID())
                .name("Smartphones")
                .code("PHONE")
                .description("Mobile phones")
                .category(category)
                .build();

        requestDTO = CategoryTypeRequestDTO.builder()
                .name("Smartphones")
                .code("PHONE")
                .description("Mobile phones")
                .build();
    }

    @Test
    void testGetAllCategoryTypes() {
        // Given
        List<CategoryType> categoryTypes = Arrays.asList(categoryType);
        when(categoryTypeRepository.findAll()).thenReturn(categoryTypes);

        // When
        List<CategoryTypeDTO> result = categoryTypeService.getAllCategoryTypes();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Smartphones", result.get(0).getName());
        verify(categoryTypeRepository).findAll();
    }

    @Test
    void testGetCategoryTypeById() {
        // Given
        UUID categoryTypeId = categoryType.getId();
        when(categoryTypeRepository.findById(categoryTypeId)).thenReturn(Optional.of(categoryType));

        // When
        CategoryTypeDTO result = categoryTypeService.getCategoryTypeById(categoryTypeId);

        // Then
        assertNotNull(result);
        assertEquals("Smartphones", result.getName());
        assertEquals("PHONE", result.getCode());
        verify(categoryTypeRepository).findById(categoryTypeId);
    }

    @Test
    void testGetCategoryTypeById_NotFound() {
        // Given
        UUID categoryTypeId = UUID.randomUUID();
        when(categoryTypeRepository.findById(categoryTypeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            categoryTypeService.getCategoryTypeById(categoryTypeId);
        });
        verify(categoryTypeRepository).findById(categoryTypeId);
    }

    @Test
    void testCreateCategoryType() {
        // Given
        UUID categoryId = category.getId();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryTypeRepository.save(any(CategoryType.class))).thenReturn(categoryType);

        // When
        CategoryTypeDTO result = categoryTypeService.createCategoryType(categoryId, requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Smartphones", result.getName());
        verify(categoryRepository).findById(categoryId);
        verify(categoryTypeRepository).save(any(CategoryType.class));
    }

    @Test
    void testCreateCategoryType_CategoryNotFound() {
        // Given
        UUID categoryId = UUID.randomUUID();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            categoryTypeService.createCategoryType(categoryId, requestDTO);
        });
        verify(categoryRepository).findById(categoryId);
        verify(categoryTypeRepository, never()).save(any());
    }

    @Test
    void testUpdateCategoryType() {
        // Given
        UUID categoryTypeId = categoryType.getId();
        CategoryTypeRequestDTO updateRequest = CategoryTypeRequestDTO.builder()
                .name("Updated Smartphones")
                .code("PHONE_UPD")
                .description("Updated description")
                .build();
        
        when(categoryTypeRepository.findById(categoryTypeId)).thenReturn(Optional.of(categoryType));
        when(categoryTypeRepository.save(any(CategoryType.class))).thenReturn(categoryType);

        // When
        CategoryTypeDTO result = categoryTypeService.updateCategoryType(categoryTypeId, updateRequest);

        // Then
        assertNotNull(result);
        verify(categoryTypeRepository).findById(categoryTypeId);
        verify(categoryTypeRepository).save(any(CategoryType.class));
    }

    @Test
    void testDeleteCategoryType() {
        // Given
        UUID categoryTypeId = categoryType.getId();
        when(categoryTypeRepository.findById(categoryTypeId)).thenReturn(Optional.of(categoryType));

        // When
        categoryTypeService.deleteCategoryType(categoryTypeId);

        // Then
        verify(categoryTypeRepository).findById(categoryTypeId);
        verify(categoryTypeRepository).delete(categoryType);
    }

    @Test
    void testDeleteCategoryType_NotFound() {
        // Given
        UUID categoryTypeId = UUID.randomUUID();
        when(categoryTypeRepository.findById(categoryTypeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            categoryTypeService.deleteCategoryType(categoryTypeId);
        });
        verify(categoryTypeRepository).findById(categoryTypeId);
        verify(categoryTypeRepository, never()).delete(any());
    }
}