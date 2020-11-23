package com.example.fms.service;

import com.example.fms.dto.CategoryDTO;
import com.example.fms.entity.Category;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface CategoryService {
    List<Category> getAll(boolean isDeleted);
    Page<Category> getByPage(List<Category> list, Pageable pageable);
    List<Category> getAllByNameContaining(String name);
    List<Category> getAllByDateCreatedAfter(String after);
    List<Category> getAllByDateCreatedBefore(String before);

    ResponseEntity<Category> addCategory(CategoryDTO categoryDTO, String userEmail);

    ResponseEntity<Category> getCategoryById(Long id);

    ResponseEntity<Category> updateCategoryById(CategoryDTO categoryDTO, Long id, String userEmail);

    ResponseMessage deleteCategoryById(Long id, String userEmail);

}
