package com.battleiq.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.Category;
import com.battleiq.dto.CategoryDTO;
import com.battleiq.dto.CategoryRequestDTO;
import com.battleiq.exception.CategoryNotFoundException;
import com.battleiq.exception.ConflictException;
import com.battleiq.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        return toDTO(findCategory(id));
    }

    @Transactional
    public CategoryDTO createCategory(CategoryRequestDTO request) {
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new ConflictException("Category name already exists: " + request.getName());
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryRequestDTO request) {
        Category category = findCategory(id);
        categoryRepository.findByName(request.getName())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ConflictException("Category name already exists: " + request.getName());
                });
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategory(id);
        try {
            categoryRepository.delete(category);
            categoryRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            // หมวดหมู่ที่มีรอบการเล่นอ้างถึงอยู่จะลบไม่ได้
            throw new ConflictException("Category " + id + " is used in quiz sessions and cannot be deleted");
        }
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + id));
    }

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}