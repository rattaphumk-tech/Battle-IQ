package com.battleiq.service.impl;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.battleiq.domain.entity.Category;
import com.battleiq.dto.CategoryDTO;
import com.battleiq.dto.CategoryRequestDTO;
import com.battleiq.exception.CategoryNotFoundException;
import com.battleiq.exception.ConflictException;
import com.battleiq.mapper.CategoryMapper;
import com.battleiq.repository.CategoryRepository;
import com.battleiq.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        return CategoryMapper.toDTO(findCategory(id));
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryRequestDTO request) {
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new ConflictException("Category name already exists: " + request.getName());
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return CategoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
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
        return CategoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
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
}
