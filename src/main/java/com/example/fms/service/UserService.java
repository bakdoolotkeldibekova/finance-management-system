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
        List<User> getAllByPosition(String position);
        List<User> getAllByActive(boolean isActive);

        List<User> getAllByDateCreatedAfter(String after);
        List<User> getAllByDateCreatedBefore(String before);
        List<User> getAllByName(String name);
        List<User> getAllBySurname(String surname);

        User getByEmail(String email);
        boolean deleteUserById(Long id);
    }
