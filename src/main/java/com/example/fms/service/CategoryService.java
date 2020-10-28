package com.example.fms.service;

import com.example.fms.entity.Category;
import com.example.fms.entity.User;

import java.util.List;

public interface CategoryService {
    List<Category> getAll();

    Category addCategory(Category newCategory, String userEmail);

    Category getCategoryById(Long id) throws Exception;

    Category updateCategoryById(Category newCategory, String userEmail) throws Exception;

    boolean deleteCategoryById(Long id, String userEmail);

}
