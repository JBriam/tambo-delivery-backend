package com.tambo.tambo_delivery_backend.services;

import com.tambo.tambo_delivery_backend.auth.entities.User;
import com.tambo.tambo_delivery_backend.dto.AddressDTO;
import com.tambo.tambo_delivery_backend.dto.AddressRequestDTO;
import com.tambo.tambo_delivery_backend.entities.Address;
import com.tambo.tambo_delivery_backend.mapper.AddressMapper;
import com.tambo.tambo_delivery_backend.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddressService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    // Obtener todas las direcciones del usuario
    public List<AddressDTO> getUserAddresses(Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        return addressRepository.findByUserOrderByIsPrimaryDescUpdatedAtDesc(user)
                .stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    // Obtener una dirección por ID
    public AddressDTO getAddressById(UUID id, Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Verificar que la dirección pertenece al usuario
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        return addressMapper.toDto(address);
    }

    // Crear una nueva dirección
    @Transactional
    public AddressDTO createAddress(AddressRequestDTO addressRequest, Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        
        // Si es direcciones primaria, desmarcar otras como primarias
        if (addressRequest.isPrimary()) {
            setPrimaryAddress(user, null);
        }
        
        Address address = addressMapper.toEntity(addressRequest, user);
        return addressMapper.toDto(addressRepository.save(address));
    }

    // Actualizar una dirección
    @Transactional
    public AddressDTO updateAddress(UUID id, AddressRequestDTO addressRequest, Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Address existing = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Verificar que la dirección pertenece al usuario
        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        // Si se está marcando como primaria, desmarcar otras
        if (addressRequest.isPrimary()) {
            setPrimaryAddress(user, id);
        }
        
        // Actualizar campos
        existing.setAlias(addressRequest.getAlias());
        existing.setAddress(addressRequest.getAddress());
        existing.setDistrict(addressRequest.getDistrict());
        existing.setCity(addressRequest.getCity());
        existing.setCountry(addressRequest.getCountry());
        existing.setLatitude(addressRequest.getLatitude());
        existing.setLongitude(addressRequest.getLongitude());
        existing.setFloor(addressRequest.getFloor());
        existing.setOffice(addressRequest.getOffice());
        existing.setApartment(addressRequest.getApartment());
        existing.setReference(addressRequest.getReference());
        existing.setIsPrimary(addressRequest.isPrimary());
        
        return addressMapper.toDto(addressRepository.save(existing));
    }

    // Marcar una dirección como primaria
    @Transactional
    public AddressDTO setPrimaryAddress(UUID id, Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Verificar que la dirección pertenece al usuario
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        // Desmarcar todas las direcciones del usuario como primarias
        setPrimaryAddress(user, id);
        
        // Marcar la dirección actual como primaria
        address.setIsPrimary(true);
        return addressMapper.toDto(addressRepository.save(address));
    }

    // Eliminar una dirección
    @Transactional
    public void deleteAddress(UUID id, Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Verificar que la dirección pertenece al usuario
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        addressRepository.delete(address);
    }

    // Método auxiliar para desmarcar direcciones como primarias
    private void setPrimaryAddress(User user, UUID excludeId) {
        List<Address> userAddresses = addressRepository.findByUserOrderByUpdatedAtDesc(user);
        for (Address addr : userAddresses) {
            if (excludeId == null || !addr.getId().equals(excludeId)) {
                addr.setIsPrimary(false);
                addressRepository.save(addr);
            }
        }
    }
}
