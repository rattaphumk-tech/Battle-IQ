package com.battleiq.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.battleiq.service.impl.CategoryServiceImpl;
import com.battleiq.domain.entity.Category;
import com.battleiq.dto.CategoryDTO;
import com.battleiq.dto.CategoryRequestDTO;
import com.battleiq.exception.CategoryNotFoundException;
import com.battleiq.exception.ConflictException;
import com.battleiq.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryRequestDTO request(String name) {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName(name);
        request.setDescription("desc");
        return request;
    }

    @Test
    void createCategorySavesAndReturnsDTO() {
        when(categoryRepository.findByName("Math")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(Category.builder().id(5L).name("Math").description("desc").build());

        CategoryDTO result = categoryService.createCategory(request("Math"));

        assertEquals(5L, result.getId());
        assertEquals("Math", result.getName());
    }

    @Test
    void createCategoryRejectsDuplicateName() {
        when(categoryRepository.findByName("Math"))
                .thenReturn(Optional.of(Category.builder().id(1L).name("Math").build()));

        assertThrows(ConflictException.class, () -> categoryService.createCategory(request("Math")));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategoryThrowsWhenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(99L, request("Math")));
    }
}