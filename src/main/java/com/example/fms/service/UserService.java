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
    void createUser(User user);

    User setPosition(String position, String userEmail);

    List<User> getAll();
    List<User> getAllByNameOrSurname(String nameOrSurname);
    List<User> getAllByPosition(String position);
    List<User> getAllByActive(boolean isActive);
    List<User> getAllByDateCreatedBetween(String after, String before);

    User getByEmail(String email);
}
