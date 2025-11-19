package com.tambo.tambo_delivery_backend.controllers;

import com.tambo.tambo_delivery_backend.dto.request.AddressRequestDTO;
import com.tambo.tambo_delivery_backend.dto.response.AddressDTO;
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
        List<AddressDTO> addresses = addressService.getUserAddresses(principal);
        return ResponseEntity.ok(addresses);
    }

    // Crear una nueva dirección
    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(@RequestBody AddressRequestDTO addressRequest,
            Principal principal) {
        AddressDTO address = addressService.createAddress(addressRequest, principal);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    // Eliminar una dirección
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable UUID id) {
        addressService.deleteAddress(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
