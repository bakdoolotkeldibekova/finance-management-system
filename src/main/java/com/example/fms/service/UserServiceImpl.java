package com.example.fms.service;

import com.example.fms.dto.UserAdminDTO;
import com.example.fms.dto.UserDTO;
import com.example.fms.dto.UserRegistrDTO;
import com.example.fms.entity.Journal;
import com.example.fms.entity.Role;
import com.example.fms.entity.User;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.RoleRepository;
import com.example.fms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public boolean save(UserRegistrDTO userRegistrDTO) {
        User user = userRepository.findByEmail(userRegistrDTO.getEmail());
        if (user != null){
            user.setPassword(encoder.encode(userRegistrDTO.getPassword()));
            user.setName(userRegistrDTO.getName());
            user.setSurname(userRegistrDTO.getSurname());
            userRepository.save(user);

            Journal journal = new Journal();
            journal.setTable("USER: " + user.getEmail());
            journal.setAction("create");
            journal.setUser(null);
            journal.setDeleted(false);
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
        user.setRole(roleRepository.findById(2L).orElse(roleRepository.save(new Role("ROLE_USER"))));

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
        user.setRole(roleRepository.findById(1L).orElse(roleRepository.save(new Role("ROLE_ADMIN"))));

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

        //System.out.println("eeeeeee: " + email + "  :  "+ user);
        return user != null && mailService.send(user.getEmail(), "Change password", message);
    }

    @Override
    public boolean changePassword(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null){
            user.setPassword(encoder.encode(password));
            userRepository.save(user);

            Journal journal = new Journal();
            journal.setTable("USER: " + user.getEmail());
            journal.setAction("changed password");
            journal.setUser(null);
            journal.setDeleted(false);
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
    public List<User> getAllByPosition(String position) {
        return userRepository.findAllByPositionContainingIgnoringCase(position);
    }

    @Override
    public List<User> getAllByActive(boolean isActive) {
        return userRepository.findAllByActive(isActive);
    }

    @Override
    public List<User> getAllByDateCreatedAfter(String after) {
        //  String str = "2016-03-04 11:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(after, formatter);
        return userRepository.findAllByDateCreatedAfter(dateTime);
    }

    @Override
    public List<User> getAllByDateCreatedBefore(String before) {
        //  String str = "2016-03-04 11:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(before, formatter);
        return userRepository.findAllByDateCreatedBefore(dateTime);
    }

    @Override
    public List<User> getAllByName(String name) {
        return userRepository.findAllByNameContainingIgnoringCase(name);
    }

    @Override
    public List<User> getAllBySurname(String surname) {
        return userRepository.findAllBySurnameContainingIgnoringCase(surname);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }
/*
    @Override
    public User getEmail(String email) {
        Map<String, Object> addUser = new HashMap<>();
        User user = userRepository.findByEmail(email);
        addUser.put("id", user.getId());
        addUser.put("dateCreated", user.getDateCreated());
        addUser.put("dateUpdated", user.getDateUpdated());
        addUser.put("email", user.getEmail());
        addUser.put("name", user.getName() + " " + user.getSurname());
        addUser.put("position", user.getPosition());

        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(addUser);
        return gson.fromJson(jsonElement, User.class);
    }

 */
    @Override
    public boolean deleteUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {

            userRepository.deleteById(id);

            Journal journal = new Journal();
            journal.setTable("USER: " + user.getEmail());
            journal.setAction("delete");
            journal.setUser(null);
            journal.setDeleted(false);
            journalRepository.save(journal);
            return true;
        }
        return false;
    }

    @Override //for Init class
    public void createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User setPosition(String position, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        user.setPosition(position);

        Journal journal = new Journal();
        journal.setTable("USER: " + user.getEmail());
        journal.setAction("changed position");
        journal.setUser(null);
        journal.setDeleted(false);
        journalRepository.save(journal);

        return userRepository.save(user);
    }


}