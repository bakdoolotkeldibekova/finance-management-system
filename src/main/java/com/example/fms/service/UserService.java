package com.example.fms.service;

import com.example.fms.dto.UserAdminDTO;
import com.example.fms.dto.UserDTO;
import com.example.fms.dto.UserRegistrDTO;
import com.example.fms.entity.User;

import java.util.List;

public interface UserService {
    boolean save(UserRegistrDTO userRegistrDTO);
    boolean createUser(UserDTO userDTO);
    void createAdmin(UserAdminDTO userAdminDTO);
    String activateUser(String code);
    boolean sendForgotPassword(String email);
    boolean changePassword(String email, String password);

    List<User> getAll();
    User getByEmail(String email);

    void createUser(User user);
}
