package com.example.fms.controller;

import com.example.fms.dto.CategoryDTO;
import com.example.fms.entity.Account;
import com.example.fms.entity.Category;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.User;
import com.example.fms.service.CategoryService;
import com.example.fms.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final UserService userService;

    CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping("/get")
    public List<Category> getAllByParam(HttpServletRequest request, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get category information");
        }
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

    @GetMapping("/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get account information");
        }
        Category category = categoryService.getCategoryById(categoryId);
        if (category != null) {
            return category;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category id " + categoryId + " not found!");
    }

    @PostMapping("/add")
    public ResponseMessage addCategory (@RequestBody CategoryDTO categoryDTO, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to add category");
        }
        categoryService.addCategory(categoryDTO, principal.getName());
        return new ResponseMessage(HttpStatus.OK.value(), "Category successfully saved");
    }

    @PutMapping("/update/{id}")
    public ResponseMessage updateCategory(@RequestBody CategoryDTO categoryDTO, @PathVariable Long id, Principal principal){
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to change category information");
        }
        Category category = categoryService.updateCategoryById(categoryDTO, id, principal.getName());
        if (category != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Category successfully updated");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category id " + id + " not found!");
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteCategory(@PathVariable Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to delete category information");
        }
        Category category = categoryService.deleteCategoryById(id, principal.getName());
        if (category != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Category successfully deleted");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category id " + id + " not found!");
    }
}
