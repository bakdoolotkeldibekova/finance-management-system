package com.example.fms.controller;

import com.example.fms.entity.Category;
import com.example.fms.entity.User;
import com.example.fms.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/getAll")
    public List<Category> getAll() {
        return categoryService.getAll();
    }

    @PostMapping("/add")
    public Category addCategory (@RequestBody Category newCategory, Principal principal) {
        return categoryService.addCategory(newCategory, principal.getName());
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable Long id) throws Exception {
        return categoryService.getCategoryById(id);
    }

    @PutMapping("/update")
    public Category updateCategory(@RequestBody Category newCategory, Principal principal) throws Exception{
        return categoryService.updateCategoryById(newCategory, principal.getName());
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteCategory(@PathVariable Long id, Principal principal) {
        return categoryService.deleteCategoryById(id, principal.getName());
    }


}
