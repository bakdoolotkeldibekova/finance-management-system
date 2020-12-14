package com.example.fms.service;

import com.example.fms.dto.CategoryDTO;
import com.example.fms.entity.*;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.CategoryRepository;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.UserRepository;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getAll(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedCategoryFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<Category> categories = categoryRepository.findAllByOrderByDateCreatedDesc();
        session.disableFilter("deletedCategoryFilter");
        return categories;
    }

    @Override
    public Page<Category> getByPage(List<Category> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        List<Category> output = new ArrayList<>();
        if (start <= end) {
            output = list.subList(start, end);
        }
        return new PageImpl<>(output, pageable, list.size());
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
    public ResponseEntity<Category> addCategory(CategoryDTO categoryDTO, String userEmail) {
        Category category = new Category(categoryDTO.getName());
        categoryRepository.save(category);

        Journal journal = new Journal();
        journal.setTable("CATEGORY: " + categoryDTO.getName());
        journal.setAction("create");
        journal.setUser(userRepository.findByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return ResponseEntity.ok().body(category);
    }

    @Override
    public ResponseEntity<Category> getCategoryById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Category id " + id + " not found!"));
        if (category.isDeleted())
            throw new ResourceNotFoundException("Category id " + id + " was deleted!");
        return ResponseEntity.ok().body(category);
    }

    @Override
    public ResponseEntity<Category> updateCategoryById(CategoryDTO categoryDTO, Long id, String userEmail){
        Category result = categoryRepository.findById(id)
                .map(category -> {
                    if (category.isDeleted())
                        throw new ResourceNotFoundException("Category id " + id + " was deleted!");
                    category.setName(categoryDTO.getName());
                    return categoryRepository.save(category);
                })
                .orElseThrow(()->new ResourceNotFoundException("Category id " + id + " not found!"));

            Journal journal = new Journal();
            journal.setTable("CATEGORY: " + categoryDTO.getName());
            journal.setAction("update");
            journal.setUser(userRepository.findByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);

        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseMessage deleteCategoryById(Long id, String userEmail) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Category id " + id + " not found!"));

        if (category.isDeleted())
            throw new ResourceNotFoundException("Category id " + id + " was deleted!");

        categoryRepository.deleteById(id);
            Journal journal = new Journal();
            journal.setTable("CATEGORY: " + category.getName());
            journal.setAction("delete");
            journal.setUser(userRepository.findByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);

        return new ResponseMessage(HttpStatus.OK.value(), "Category successfully deleted");
    }
}
