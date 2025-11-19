package com.tambo.tambo_delivery_backend.dto.response;

import java.util.UUID;

import com.google.common.base.MoreObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDTO {
    private UUID id;
    private String name;
    private String description;
    private String imageUrl;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("description", description)
                .add("imageUrl", imageUrl)
                .toString();
    }
}
