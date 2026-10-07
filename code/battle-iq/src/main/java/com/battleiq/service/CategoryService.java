package com.battleiq.service;

import java.util.List;

import com.battleiq.dto.CategoryDTO;
import com.battleiq.dto.CategoryRequestDTO;

public interface CategoryService {

    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategoryById(Long id);

    CategoryDTO createCategory(CategoryRequestDTO request);

    CategoryDTO updateCategory(Long id, CategoryRequestDTO request);

    void deleteCategory(Long id);
}
