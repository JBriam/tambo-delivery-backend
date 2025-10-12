package com.tambo.tambo_delivery_backend.controllers;

import com.tambo.tambo_delivery_backend.dto.AddressDTO;
import com.tambo.tambo_delivery_backend.dto.AddressRequestDTO;
import com.tambo.tambo_delivery_backend.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/address")
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

    // Obtener todas las direcciones del usuario autenticado
    @GetMapping
    public ResponseEntity<List<AddressDTO>> getUserAddresses(Principal principal) {
        try {
            List<AddressDTO> addresses = addressService.getUserAddresses(principal);
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener una dirección específica por ID
    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable UUID id, Principal principal) {
        try {
            AddressDTO address = addressService.getAddressById(id, principal);
            return ResponseEntity.ok(address);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Crear una nueva dirección
    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(@RequestBody AddressRequestDTO addressRequest,
            Principal principal) {
        try {
            AddressDTO address = addressService.createAddress(addressRequest, principal);
            return new ResponseEntity<>(address, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Actualizar una dirección existente
    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable UUID id, 
                                                   @RequestBody AddressRequestDTO addressRequest,
                                                   Principal principal) {
        try {
            AddressDTO address = addressService.updateAddress(id, addressRequest, principal);
            return ResponseEntity.ok(address);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Marcar una dirección como primaria
    @PutMapping("/{id}/primary")
    public ResponseEntity<AddressDTO> setPrimaryAddress(@PathVariable UUID id, Principal principal) {
        try {
            AddressDTO address = addressService.setPrimaryAddress(id, principal);
            return ResponseEntity.ok(address);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Eliminar una dirección
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable UUID id, Principal principal) {
        try {
            addressService.deleteAddress(id, principal);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
