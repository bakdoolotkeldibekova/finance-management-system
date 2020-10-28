package com.example.fms.service;

import com.example.fms.dto.UserAdminDTO;
import com.example.fms.dto.UserDTO;
import com.example.fms.dto.UserRegistrDTO;
import com.example.fms.entity.Journal;
import com.example.fms.entity.Role;
import com.example.fms.entity.User;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JournalRepository journalRepository;

    @Override
    public boolean save(UserRegistrDTO userRegistrDTO) {
        User user = userRepository.findByEmail(userRegistrDTO.getEmail());
        if (user != null){
            user.setPassword(encoder.encode(userRegistrDTO.getPassword()));
            user.setName(userRegistrDTO.getName());
            user.setSurname(userRegistrDTO.getSurname());
            userRepository.save(user);

            Journal journal = new Journal();
            journal.setAction1("USER");
            journal.setAction2("create");
            journal.setUser(user);
            journalRepository.save(journal);
            return true;
        }
        return false;
    }

    @Override
    public boolean createUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
      //  user.setDepartments(userDTO.getDepartmentList());
        user.setActive(false);
        user.setActivationCode(UUID.randomUUID().toString());

        Role role = new Role();
        role.setName("USER");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);

        String message = "Hello, ! \n" +
                " Please, visit next link to activate your account: http:localhost:8080/registr/activate/" +
                user.getActivationCode();
        if(mailService.send(user.getEmail(), "Activation Code", message)){
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void createAdmin(UserAdminDTO userAdminDTO) {
        User user = new User();
        user.setEmail(userAdminDTO.getEmail());
        user.setName(userAdminDTO.getName());
        user.setSurname(userAdminDTO.getSurname());
        user.setPassword(encoder.encode(userAdminDTO.getPassword()));
        user.setPosition(userAdminDTO.getPosition());
        user.setActive(true);

        Role role = new Role();
        role.setName("ADMIN");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Override
    public String activateUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if (user == null) {
            return "error";
        }
        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);
        return user.getEmail();
    }

    @Override
    public boolean sendForgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        String message = "Hello, ! \n" +
                " Please, visit next link to change your password: http:localhost:8080/registr/changePassword";

        return user != null && mailService.send(user.getEmail(), "Change password", message);
    }

    @Override
    public boolean changePassword(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null){
            user.setPassword(encoder.encode(password));
            userRepository.save(user);

            Journal journal = new Journal();
            journal.setAction1("USER");
            journal.setAction2("changed password");
            journal.setUser(userRepository.findByEmail(email));
            journalRepository.save(journal);

            return true;
        }
        return false;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }


}