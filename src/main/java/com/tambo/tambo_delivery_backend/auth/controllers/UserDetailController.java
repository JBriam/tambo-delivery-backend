package com.tambo.tambo_delivery_backend.auth.controllers;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tambo.tambo_delivery_backend.auth.dto.request.UserUpdateDto;
import com.tambo.tambo_delivery_backend.auth.dto.response.UserDetailsDto;
import com.tambo.tambo_delivery_backend.auth.dto.response.UserResponseDto;
import com.tambo.tambo_delivery_backend.auth.entities.User;
import com.tambo.tambo_delivery_backend.auth.services.UserService;
import com.tambo.tambo_delivery_backend.dto.response.AddressDTO;
import com.tambo.tambo_delivery_backend.dto.response.OrderDetails;
import com.tambo.tambo_delivery_backend.entities.OrderStatus;
import com.tambo.tambo_delivery_backend.services.AddressService;
import com.tambo.tambo_delivery_backend.services.OrderService;

// Proporcionar un endpoint seguro para que los usuarios autenticados obtengan su información de perfil
@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserDetailController {

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private UserService userService;

        @Autowired
        private AddressService addressService;

        @Autowired
        private OrderService orderService;

        // Método GET que devuelve los detalles del usuario
        @GetMapping("/profile")
        public ResponseEntity<UserDetailsDto> getUserProfile(Principal principal) {
                User user = (User) userDetailsService.loadUserByUsername(principal.getName());

                // Verificar si el usuario autenticado existe en el sistema
                if (null == user) {
                        // responde con código HTTP 401 Unauthorized
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }

                // Transforma la entidad User en un DTO (UserDetailsDto) para la respuesta
                UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .profileImageUrl(user.getProfileImageUrl())
                                .email(user.getEmail())
                                .phoneNumber(user.getPhoneNumber())
                                .enabled(user.isEnabled())
                                .authorityList(user.getAuthorities().stream()
                                                .map(auth -> auth.getAuthority()).toList())
                                .build();

                // Retorna los datos con estado HTTP 200 (OK) si todo es correcto
                return new ResponseEntity<>(userDetailsDto, HttpStatus.OK);

        }

        @PutMapping("/update")
        public ResponseEntity<UserResponseDto> updateUser(@RequestBody UserUpdateDto request) {
                // Verificar si el usuario autenticado existe en el sistema
                User user = (User) userDetailsService.loadUserByUsername(request.getEmail());

                if (null != user) {
                        UserResponseDto updateResponse = userService.updateUser(request);
                        // responde con código HTTP 401 Unauthorized
                        return new ResponseEntity<>(updateResponse, HttpStatus.OK);
                } else {
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }

        }

        // Obtener todas las direcciones del usuario autenticado
        @GetMapping("/getMyAddressses")
        public ResponseEntity<List<AddressDTO>> getUserAddresses(Principal principal) {
                List<AddressDTO> addresses = addressService.getUserAddresses(principal);
                return ResponseEntity.ok(addresses);
        }

        // Obtener los pedidos del usuario
        @GetMapping("/order")
        public ResponseEntity<List<OrderDetails>> getOrdersByUser(Principal principal) {
                List<OrderDetails> orders = orderService.getOrdersByUser(principal.getName());
                return new ResponseEntity<>(orders, HttpStatus.OK);
        }

        // Cancelar un pedido
        @PostMapping("/cancelMyOrder")
        public ResponseEntity<?> cancellOrderByOrderId(Principal principal, @RequestBody String orderIdStr) {
                // 1. Validar usuario
                User user = (User) userDetailsService.loadUserByUsername(principal.getName());
                if (user == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
                }

                // 2. Validar UUID
                if (orderIdStr == null || orderIdStr.isEmpty()) {
                        return ResponseEntity.badRequest().body("ID de orden no proporcionado");
                }

                try {
                        UUID orderId = UUID.fromString(orderIdStr.trim());

                        // 3. Validar si la orden puede cancelarse (añade esta lógica en tu servicio)
                        if (!orderService.canOrderBeCancelled(orderId)) {
                                return ResponseEntity.badRequest()
                                                .body("La orden no puede cancelarse en su estado actual");
                        }

                        // 4. Actualizar orden
                        orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED, null);
                        return ResponseEntity.ok().build();

                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body("ID de orden inválido");
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().body("Error al procesar la solicitud");
                }
        }

}
