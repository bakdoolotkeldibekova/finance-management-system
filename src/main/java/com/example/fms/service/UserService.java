    package com.example.fms.service;

    import com.example.fms.dto.UserAdminDTO;
    import com.example.fms.dto.UserDTO;
    import com.example.fms.dto.UserRegistrDTO;
    import com.example.fms.entity.ResponseMessage;
    import com.example.fms.entity.User;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.util.List;

    public interface UserService {
        ResponseEntity<User> save(UserRegistrDTO userRegistrDTO);
        ResponseMessage createUser(UserDTO userDTO);
        void createAdmin(UserAdminDTO userAdminDTO);
        ResponseMessage activateUser(String code);
        ResponseMessage sendForgotPassword(String email);
        ResponseEntity<User> changeForgotPassword(String email, String newPassword, String dateTime);
        ResponseEntity<User> changePassword(String email, String newPassword);
        void createUser(User user);

        ResponseEntity<User> setPosition(String position, String userEmail);
        ResponseEntity<User> setImage(MultipartFile multipartFile, String userEmail) throws IOException;
        ResponseMessage deleteImage(String email);

        List<User> getAll();
        Page<User> getByPage(List<User> list, Pageable pageable);
        List<User> getAllByPosition(String position);
        List<User> getAllByActive(boolean isActive);

        List<User> getAllByDateCreatedAfter(String after);
        List<User> getAllByDateCreatedBefore(String before);
        List<User> getAllByName(String name);
        List<User> getAllBySurname(String surname);

        ResponseEntity<User> getByEmail(String email);
        ResponseEntity<User> getById(Long id);
        ResponseMessage blockUserById(Long id, String userEmail);
        ResponseEntity<User> unBlockUserById(Long id, String userEmail);
    }
