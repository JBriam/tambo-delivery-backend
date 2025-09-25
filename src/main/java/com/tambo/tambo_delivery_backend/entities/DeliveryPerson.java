package com.tambo.tambo_delivery_backend.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPerson {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String phone;

    private String vehicleType;

    private String plateNumber;

    private Boolean isAvailable;
}
