package com.tambo.tambo_delivery_backend.services;

import com.tambo.tambo_delivery_backend.auth.entities.User;
import com.tambo.tambo_delivery_backend.dto.request.AddressRequestDTO;
import com.tambo.tambo_delivery_backend.dto.response.AddressDTO;
import com.tambo.tambo_delivery_backend.entities.Address;
import com.tambo.tambo_delivery_backend.mapper.AddressMapper;
import com.tambo.tambo_delivery_backend.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

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
        return addressRepository.findByUserOrderByUpdatedAtDesc(user)
                .stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    // Crear una nueva dirección
    public AddressDTO createAddress(AddressRequestDTO addressRequest, Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Address address = addressMapper.toEntity(addressRequest, user);
        return addressMapper.toDto(addressRepository.save(address));
    }

    // Eliminar una dirección
    public void deleteAddress(UUID id) {
        addressRepository.deleteById(id);
    }

}
