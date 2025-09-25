package com.tambo.tambo_delivery_backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserRequestDtoAdmin {

    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String phoneNumber;
    private String email;
    private List<String> roles;

}
