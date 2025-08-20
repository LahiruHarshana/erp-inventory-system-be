package com.erp.inventory.system.service.impl;

import com.erp.inventory.system.controller.CategoryDto;
import com.erp.inventory.system.model.Category;
import com.erp.inventory.system.repository.CategoryRepository;
import com.erp.inventory.system.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        categoryRepository.findByName(categoryDto.getName()).ifPresent(c -> {
            throw new IllegalStateException("Category with name '" + c.getName() + "' already exists.");
        });
        Category category = new Category();
        category.setName(categoryDto.getName());
        Category savedCategory = categoryRepository.save(category);
        return mapToDto(savedCategory);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id: " + id);
        }
        // Add logic here to check if category is in use before deleting
        categoryRepository.deleteById(id);
    }

    private CategoryDto mapToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
