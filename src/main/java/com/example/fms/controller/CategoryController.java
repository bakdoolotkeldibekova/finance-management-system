package com.example.fms.controller;

import com.example.fms.dto.CategoryDTO;
import com.example.fms.entity.Category;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.service.CategoryService;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/get")
    public Page<Category> getAllByParam(Pageable pageable,
                                        @RequestParam(value = "isDeleted", required = false, defaultValue = "false") boolean isDeleted,
                                        @RequestParam(required = false) String name,
                                        @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                        @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore) {

        Set<Category> fooSet = new LinkedHashSet<>(categoryService.getAll(isDeleted));

        if (name != null)
            fooSet.retainAll(categoryService.getAllByNameContaining(name));
        if (dateAfter != null)
            fooSet.retainAll(categoryService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(categoryService.getAllByDateCreatedBefore(dateBefore));

        List<Category> list = new ArrayList<>(fooSet);
        return categoryService.getByPage(list, pageable);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getById(@PathVariable("categoryId") Long categoryId) {
        return categoryService.getCategoryById(categoryId);
      }

    @PostMapping("/add")
    public ResponseEntity<Category> addCategory (@RequestBody CategoryDTO categoryDTO, Principal principal) {
        return categoryService.addCategory(categoryDTO, principal.getName());
      }

    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@RequestBody CategoryDTO categoryDTO, @PathVariable Long id, Principal principal){
        return categoryService.updateCategoryById(categoryDTO, id, principal.getName());
      }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteCategory(@PathVariable Long id, Principal principal) {
        return categoryService.deleteCategoryById(id, principal.getName());
    }
}
