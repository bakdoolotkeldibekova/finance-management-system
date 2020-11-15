package com.example.fms.service;

import com.example.fms.dto.CategoryDTO;
import com.example.fms.entity.Category;
import com.example.fms.entity.Journal;
import com.example.fms.repository.CategoryRepository;
import com.example.fms.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserService userService;

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getAllByNameContaining(String name) {
        return categoryRepository.findAllByNameContainingIgnoringCase(name);
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public List<Category> getAllByDateCreatedAfter(String after) {
        return categoryRepository.findAllByDateCreatedAfter(LocalDateTime.parse(after, formatter));
    }

    @Override
    public List<Category> getAllByDateCreatedBefore(String before) {
        return categoryRepository.findAllByDateCreatedBefore(LocalDateTime.parse(before, formatter));
    }

    @Override
    public Category addCategory(CategoryDTO categoryDTO, String userEmail) {
        Category category = new Category(categoryDTO.getName());

        Journal journal = new Journal();
        journal.setTable("CATEGORY: " + categoryDTO.getName());
        journal.setAction("create");
        journal.setUser(userService.getByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id){
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public Category updateCategoryById(CategoryDTO categoryDTO, Long id, String userEmail){
        Category result = categoryRepository.findById(id)
                .map(category -> {
                    category.setName(categoryDTO.getName());
                    return categoryRepository.save(category);
                })
                .orElse(null);
        if (result != null) {
            Journal journal = new Journal();
            journal.setTable("CATEGORY: " + categoryDTO.getName());
            journal.setAction("update");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
        }
        return result;
    }

    @Override
    public Category deleteCategoryById(Long id, String userEmail) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null){
            categoryRepository.deleteById(id);
            Journal journal = new Journal();
            journal.setTable("CATEGORY: " + category.getName());
            journal.setAction("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
        }
        return category;
    }
}
