package com.example.fms.service;

import com.example.fms.entity.Category;
import com.example.fms.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CategoryService {
    List<Category> getAll();
    List<Category> getAllByNameContaining(String name);
    List<Category> getAllByDateCreatedAfter(String after);
    List<Category> getAllByDateCreatedBefore(String before);

    Category addCategory(Category newCategory, String userEmail);

    Category getCategoryById(Long id) throws Exception;

    Category updateCategoryById(Category newCategory, String userEmail) throws Exception;

    boolean deleteCategoryById(Long id, String userEmail);

}
