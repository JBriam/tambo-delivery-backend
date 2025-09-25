package com.tambo.tambo_delivery_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

import com.google.common.base.MoreObjects;

@Data
@Builder
public class BrandDTO {
    private UUID id;
    private String name;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .toString();
    }
}
