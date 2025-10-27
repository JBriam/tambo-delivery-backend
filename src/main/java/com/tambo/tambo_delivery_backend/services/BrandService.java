package com.tambo.tambo_delivery_backend.services;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.tambo.tambo_delivery_backend.dto.request.BrandRequest;
import com.tambo.tambo_delivery_backend.dto.response.BrandDTO;
import com.tambo.tambo_delivery_backend.entities.Brand;
import com.tambo.tambo_delivery_backend.repositories.BrandRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService {

    private static final Logger log = LoggerFactory.getLogger(BrandService.class);

    private final BrandRepository brandRepository;

    public BrandDTO createBrand(BrandRequest request) {
        Preconditions.checkNotNull(request, "La solicitud no puede ser nula");
        Preconditions.checkArgument(
                request.getName() != null && !request.getName().trim().isEmpty(),
                "El nombre de la marca no puede estar vacío");

        Brand brand = new Brand();
        brand.setName(request.getName().trim());
        brand.setDescription(request.getDescription().trim());
        brand.setImageUrl(request.getImageUrl().trim());

        Brand saved = brandRepository.save(brand);

        return mapToDTO(saved);
    }

    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(ImmutableList.toImmutableList());
    }

    // Cache para almacenar marcas por ID
    private final Cache<UUID, BrandDTO> brandCache = CacheBuilder.newBuilder()
            .maximumSize(100) // Máximo 100 elementos en caché
            .expireAfterWrite(10, TimeUnit.MINUTES) // Expira después de 10 minutos
            .build();

    public BrandDTO getBrandById(UUID id) {
        log.debug("Buscando marca con ID: {}", id); // Solo visible si level=DEBUG

        try {
            // Primero intentamos obtener del caché
            return brandCache.get(id, () -> {
                Brand brand = brandRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Marca no encontrado"));
                log.info("Marca encontrada: {}", brand.getName());
                return mapToDTO(brand);
            });
        } catch (ExecutionException e) {
            log.info("Marca no encontrada");
            throw new RuntimeException("Error al obtener la marca", e);
        }
    }

    public BrandDTO updateBrand(UUID id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marca no encontrado"));

        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setImageUrl(request.getImageUrl());
        Brand updated = brandRepository.save(brand);

        // Actualizamos el caché
        BrandDTO updatedDTO = mapToDTO(updated);
        brandCache.put(id, updatedDTO);

        return updatedDTO;
    }

    public void deleteBrand(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marca no encontrado"));
        brandRepository.delete(brand);

        // Invalidamos la entrada en el caché
        brandCache.invalidate(id);
    }

    private BrandDTO mapToDTO(Brand brand) {
        BrandDTO dto = BrandDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .imageUrl(brand.getImageUrl())
                .build();
        return dto;
    }
}
