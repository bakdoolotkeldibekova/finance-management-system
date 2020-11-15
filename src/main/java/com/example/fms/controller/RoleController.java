package com.example.fms.controller;

import com.example.fms.entity.Role;
import com.example.fms.repository.RoleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
    private RoleRepository roleRepository;

    @GetMapping
    public List<Role> getAll(){
        return roleRepository.findAll();
    }
}
