package com.erp.inventory.system.service;

import com.erp.inventory.system.controller.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);
    List<CategoryDto> getAllCategories();
    void deleteCategory(Long id);
}