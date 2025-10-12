package com.tambo.tambo_delivery_backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.security.Principal;
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
import org.springframework.security.core.userdetails.UserDetailsService;

import com.tambo.tambo_delivery_backend.auth.entities.User;
import com.tambo.tambo_delivery_backend.dto.AddressDTO;
import com.tambo.tambo_delivery_backend.dto.AddressRequestDTO;
import com.tambo.tambo_delivery_backend.entities.Address;
import com.tambo.tambo_delivery_backend.mapper.AddressMapper;
import com.tambo.tambo_delivery_backend.repositories.AddressRepository;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private Principal principal;

    @InjectMocks
    private AddressService addressService;

    private User user;
    private Address address;
    private AddressDTO addressDTO;
    private AddressRequestDTO addressRequestDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        address = Address.builder()
                .id(UUID.randomUUID())
                .alias("Casa")
                .address("123 Main St")
                .district("District")
                .city("City")
                .country("Country")
                .latitude(-12.0)
                .longitude(-77.0)
                .isPrimary(false)
                .user(user)
                .build();

        addressDTO = AddressDTO.builder()
                .id(address.getId())
                .alias("Casa")
                .address("123 Main St")
                .district("District")
                .city("City")
                .country("Country")
                .latitude(-12.0)
                .longitude(-77.0)
                .isPrimary(false)
                .build();

        addressRequestDTO = AddressRequestDTO.builder()
                .alias("Casa")
                .address("123 Main St")
                .district("District")
                .city("City")
                .country("Country")
                .latitude(-12.0)
                .longitude(-77.0)
                .isPrimary(false)
                .build();
    }

    @Test
    void testGetUserAddresses() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(addressRepository.findByUserOrderByIsPrimaryDescUpdatedAtDesc(user))
                .thenReturn(Arrays.asList(address));
        when(addressMapper.toDto(address)).thenReturn(addressDTO);

        // When
        List<AddressDTO> result = addressService.getUserAddresses(principal);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Casa", result.get(0).getAlias());
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(addressRepository).findByUserOrderByIsPrimaryDescUpdatedAtDesc(user);
    }

    @Test
    void testGetAddressById() {
        // Given
        UUID addressId = address.getId();
        when(principal.getName()).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(addressMapper.toDto(address)).thenReturn(addressDTO);

        // When
        AddressDTO result = addressService.getAddressById(addressId, principal);

        // Then
        assertNotNull(result);
        assertEquals("Casa", result.getAlias());
        verify(addressRepository).findById(addressId);
    }

    @Test
    void testGetAddressById_NotFound() {
        // Given
        UUID addressId = UUID.randomUUID();
        when(principal.getName()).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            addressService.getAddressById(addressId, principal);
        });
    }

    @Test
    void testGetAddressById_AccessDenied() {
        // Given
        UUID addressId = address.getId();
        User anotherUser = User.builder()
                .id(UUID.randomUUID())
                .email("another@example.com")
                .build();
        
        when(principal.getName()).thenReturn("another@example.com");
        when(userDetailsService.loadUserByUsername("another@example.com")).thenReturn(anotherUser);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            addressService.getAddressById(addressId, principal);
        });
    }

    @Test
    void testCreateAddress() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(addressMapper.toEntity(addressRequestDTO, user)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressDTO);

        // When
        AddressDTO result = addressService.createAddress(addressRequestDTO, principal);

        // Then
        assertNotNull(result);
        assertEquals("Casa", result.getAlias());
        verify(addressRepository).save(address);
    }

    @Test
    void testDeleteAddress() {
        // Given
        UUID addressId = address.getId();
        when(principal.getName()).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        // When
        addressService.deleteAddress(addressId, principal);

        // Then
        verify(addressRepository).findById(addressId);
        verify(addressRepository).delete(address);
    }

    @Test
    void testSetPrimaryAddress() {
        // Given
        UUID addressId = address.getId();
        when(principal.getName()).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(addressRepository.findByUserOrderByUpdatedAtDesc(user)).thenReturn(Arrays.asList(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressDTO);

        // When
        AddressDTO result = addressService.setPrimaryAddress(addressId, principal);

        // Then
        assertNotNull(result);
        assertTrue(address.getIsPrimary());
        verify(addressRepository).save(address);
    }
}