package com.battleiq.mapper;

import com.battleiq.domain.entity.Category;
import com.battleiq.dto.response.CategoryDTO;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
