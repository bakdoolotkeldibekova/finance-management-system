package com.example.fms.controller;

import com.example.fms.entity.Category;
import com.example.fms.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/get")
    public List<Category> getAllByParam(HttpServletRequest request) {
        String name = request.getParameter("name");
        String dateAfter = request.getParameter("dateAfter");
        String dateBefore = request.getParameter("dateBefore");

        Set<Category> fooSet = new LinkedHashSet<>(categoryService.getAll());

        if (name != null)
            fooSet.retainAll(categoryService.getAllByNameContaining(name));
        if (dateAfter != null)
            fooSet.retainAll(categoryService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(categoryService.getAllByDateCreatedBefore(dateBefore));

        return new ArrayList<>(fooSet);
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
