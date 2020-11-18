package com.example.fms.service;

import com.example.fms.dto.UserAdminDTO;
import com.example.fms.dto.UserDTO;
import com.example.fms.dto.UserRegistrDTO;
import com.example.fms.entity.Journal;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.Role;
import com.example.fms.entity.User;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.RoleRepository;
import com.example.fms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<User> save(UserRegistrDTO userRegistrDTO) {
        User user = userRepository.findByEmail(userRegistrDTO.getEmail());
        if (user == null)
            throw  new ResourceNotFoundException("User email " + userRegistrDTO.getEmail() + " not found!");
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

        return ResponseEntity.ok().body(user);
    }

    @Override
    public ResponseMessage createUser(UserDTO userDTO) {
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
            return new ResponseMessage(HttpStatus.OK.value(), "Invitation sent successfully");
        }
        return new ResponseMessage(HttpStatus.BAD_GATEWAY.value(), "invitation was not sent");
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
    public ResponseMessage activateUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if (user == null) {
            return new ResponseMessage(HttpStatus.NOT_FOUND.value(), "could not activate user, " + code + " this code is not true");
        }
        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);
        return new ResponseMessage(HttpStatus.OK.value(), user.getEmail() + " successfully activated");
    }

    @Override
    public ResponseMessage sendForgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            return new ResponseMessage(HttpStatus.NOT_FOUND.value(), "User email " + email + " not found!");
        String message = "Hello, ! \n" +
                " Please, visit next link to change your password: http:localhost:8080/registr/changePassword";
        if (!mailService.send(user.getEmail(), "Change password", message))
            return new ResponseMessage(HttpStatus.BAD_GATEWAY.value(), "smtp server failure, request was not sent");
        return new ResponseMessage(HttpStatus.OK.value(), "Successfully sent");
    }

    @Override
    public ResponseEntity<User> changePassword(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new ResourceNotFoundException("User email " + email + " not found!");

        user.setPassword(encoder.encode(password));
        userRepository.save(user);

        Journal journal = new Journal();
        journal.setTable("USER: " + user.getEmail());
        journal.setAction("changed password");
        journal.setUser(null);
        journal.setDeleted(false);
        journalRepository.save(journal);
        return ResponseEntity.ok().body(user);
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
    public ResponseEntity<User> getByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw  new ResourceNotFoundException("User email " + email + " not found!");
        return ResponseEntity.ok().body(user);
    }

    @Override
    public ResponseEntity<User> getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User id " + id +" not found!"));
        return ResponseEntity.ok().body(user);
    }

    @Override
    public ResponseMessage deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found!"));
        userRepository.deleteById(id);

        Journal journal = new Journal();
        journal.setTable("USER: " + user.getEmail());
        journal.setAction("delete");
        journal.setUser(null);
        journal.setDeleted(false);
        journalRepository.save(journal);

        return new ResponseMessage(HttpStatus.OK.value(), "User successfully deleted");
    }

    @Override //for Init class
    public void createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public ResponseEntity<User> setPosition(String position, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null)
            throw  new ResourceNotFoundException("User email " + userEmail + " not found!");
        user.setPosition(position);
        userRepository.save(user);

        Journal journal = new Journal();
        journal.setTable("USER: " + user.getEmail());
        journal.setAction("changed position");
        journal.setUser(null);
        journal.setDeleted(false);
        journalRepository.save(journal);

        return ResponseEntity.ok().body(user);
    }
}