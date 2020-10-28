package com.example.fms.service;

import com.example.fms.entity.Category;
import com.example.fms.entity.Journal;
import com.example.fms.entity.User;
import com.example.fms.repository.CategoryRepository;
import com.example.fms.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public Category addCategory(Category newCategory, String userEmail) {
        Journal journal = new Journal();
        journal.setAction1("CATEGORY");
        journal.setAction2("create");
        journal.setUser(userService.getByEmail(userEmail));
        journalRepository.save(journal);
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(Long id) throws Exception{
        return categoryRepository.findById(id)
                .orElseThrow(Exception::new);
    }

    @Override
    public Category updateCategoryById(Category newCategory, String userEmail) throws Exception{
        Category result = categoryRepository.findById(newCategory.getId())
                .map(category -> {
                    category.setName(newCategory.getName());
                    return categoryRepository.save(category);
                })
                .orElseThrow(Exception::new);

        Journal journal = new Journal();
        journal.setAction1("CATEGORY");
        journal.setAction2("update");
        journal.setUser(userService.getByEmail(userEmail));
        journalRepository.save(journal);

        return result;
    }

    @Override
    public boolean deleteCategoryById(Long id, String userEmail) {
        if (categoryRepository.findById(id).isPresent()){
            categoryRepository.deleteById(id);

            Journal journal = new Journal();
            journal.setAction1("CATEGORY");
            journal.setAction2("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journalRepository.save(journal);
            return true;
        }
        return false;
    }
}
