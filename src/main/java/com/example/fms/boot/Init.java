package com.example.fms.boot;

import com.example.fms.dto.UserAdminDTO;
import com.example.fms.entity.Role;
import com.example.fms.entity.User;
import com.example.fms.repository.RoleRepository;
import com.example.fms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class Init implements CommandLineRunner {

//    @Autowired
//    private UserService userService;
//    @Autowired
//    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
//        Role role = new Role("ROLE_ADMIN");
//        roleRepository.save(role);
//        Role role1 = new Role("ROLE_USER");
//        roleRepository.save(role1);
////
//   //     userService.createUser(new User("1804.01026@manas.edu.kg", "12345678", "Bulut", "Alimov", role1, "student", true, null));
//        userService.createAdmin(new UserAdminDTO("sanira@gmail.com", "Sanira", "Madzhikova", "S12345678", "head of neobis"));
    }
}