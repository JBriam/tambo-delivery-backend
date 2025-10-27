package com.tambo.tambo_delivery_backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tambo.tambo_delivery_backend.dto.request.DiscountRequestDTO;
import com.tambo.tambo_delivery_backend.dto.response.DiscountDTO;
import com.tambo.tambo_delivery_backend.entities.Discount;
import com.tambo.tambo_delivery_backend.mapper.DiscountMapper;
import com.tambo.tambo_delivery_backend.repositories.DiscountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    private final DiscountMapper discountMapper;

    // Crear un nuevo descuento
    public DiscountDTO createDiscount(DiscountRequestDTO request) {
        Discount discount = discountMapper.toEntity(request);
        Discount saved = discountRepository.save(discount);
        return discountMapper.toDTO(saved);
    }

    // Obtener todos los descuentos
    @Transactional(readOnly = true)
    public List<DiscountDTO> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(discountMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Obtener un descuento por ID
    @Transactional(readOnly = true)
    public DiscountDTO getDiscountById(UUID id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        return discountMapper.toDTO(discount);
    }

    // Actualizar un descuento
    public DiscountDTO updateDiscount(UUID id, DiscountRequestDTO request) {
        Discount existing = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        Discount updatedEntity = discountMapper.toEntity(request);
        updatedEntity.setId(existing.getId());
        Discount saved = discountRepository.save(updatedEntity);
        return discountMapper.toDTO(saved);
    }

    // Eliminar un descuento
    public void deleteDiscount(UUID id) {
        discountRepository.deleteById(id);
    }

}
