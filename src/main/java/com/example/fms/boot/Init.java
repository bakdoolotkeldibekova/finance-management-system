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

    @Autowired
    private UserService userService;
//    @Autowired
//    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
//        List<Role> roleList = new ArrayList<>();
//        roleList.add(roleRepository.save(new Role("USER")));
//        userService.createUser(new User("1812.01022@manas.edu.kg", "12345678", "Bulut", "Alimov", roleList, "student", true, null));
       // userService.createAdmin(new UserAdminDTO("sanira@gmail.com", "Sanira", "M", "S12345678", "head of neobis"));
    }
}
