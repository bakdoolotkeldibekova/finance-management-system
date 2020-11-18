    package com.example.fms.service;

    import com.example.fms.dto.UserAdminDTO;
    import com.example.fms.dto.UserDTO;
    import com.example.fms.dto.UserRegistrDTO;
    import com.example.fms.entity.ResponseMessage;
    import com.example.fms.entity.User;
    import org.springframework.http.ResponseEntity;

    import java.util.List;
    import java.util.Map;

    public interface UserService {
        ResponseEntity<User> save(UserRegistrDTO userRegistrDTO);
        ResponseMessage createUser(UserDTO userDTO);
        void createAdmin(UserAdminDTO userAdminDTO);
        ResponseMessage activateUser(String code);
        ResponseMessage sendForgotPassword(String email);
        ResponseEntity<User> changePassword(String email, String password);
        void createUser(User user);

        ResponseEntity<User> setPosition(String position, String userEmail);

        List<User> getAll();
        List<User> getAllByPosition(String position);
        List<User> getAllByActive(boolean isActive);

        List<User> getAllByDateCreatedAfter(String after);
        List<User> getAllByDateCreatedBefore(String before);
        List<User> getAllByName(String name);
        List<User> getAllBySurname(String surname);

        ResponseEntity<User> getByEmail(String email);
        ResponseEntity<User> getById(Long id);
        ResponseMessage deleteUserById(Long id);
    }
