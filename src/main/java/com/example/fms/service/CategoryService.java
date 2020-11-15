package com.example.fms.service;

import com.example.fms.dto.CategoryDTO;
import com.example.fms.entity.Category;
import com.example.fms.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CategoryService {
    List<Category> getAll();
    List<Category> getAllByNameContaining(String name);
    List<Category> getAllByDateCreatedAfter(String after);
    List<Category> getAllByDateCreatedBefore(String before);

    Category addCategory(CategoryDTO categoryDTO, String userEmail);

    Category getCategoryById(Long id);

    Category updateCategoryById(CategoryDTO categoryDTO, Long id, String userEmail);

    Category deleteCategoryById(Long id, String userEmail);

}
