package com.tambo.tambo_delivery_backend.auth.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRoleDto {

    private String roleCode;
    private String roleDescription;

}
