package com.tambo.tambo_delivery_backend.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {

    private UUID id;
    private String alias;
    private String address;
    private String district;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private String floor;
    private String office;
    private String apartment;
    private String reference;
    private boolean isPrimary;
    // private UUID userId;

}
