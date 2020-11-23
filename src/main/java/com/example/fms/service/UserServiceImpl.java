package com.example.fms.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.fms.dto.UserAdminDTO;
import com.example.fms.dto.UserDTO;
import com.example.fms.dto.UserRegistrDTO;
import com.example.fms.entity.*;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.ImageRepository;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.RoleRepository;
import com.example.fms.repository.UserRepository;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private EntityManager entityManager;

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
    public List<User> getAll(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedUsersFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<User> users = userRepository.findAll();
        session.disableFilter("deletedUsersFilter");
        return users;
    }

    @Override
    public Page<User> getByPage(List<User> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<User>(list.subList(start, end), pageable, list.size());
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
    public ResponseMessage blockUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found!"));
        user.setActive(false);
        userRepository.save(user);

        Journal journal = new Journal();
        journal.setTable("USER: " + user.getEmail());
        journal.setAction("block");
        journal.setUser(null);
        journal.setDeleted(false);
        journalRepository.save(journal);

        return new ResponseMessage(HttpStatus.OK.value(), "User successfully blocked");
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
            throw new ResourceNotFoundException("User email " + userEmail + " not found!");
        user.setPosition(position);
        userRepository.save(user);

        Journal journal = new Journal();
        journal.setTable("USER: " + user.getEmail());
        journal.setAction("changed position");
        journal.setUser(user);
        journal.setDeleted(false);
        journalRepository.save(journal);

        return ResponseEntity.ok().body(user);
    }

    @Override
    public ResponseEntity<User> setImage(MultipartFile multipartFile, String userEmail) throws IOException {

        final String urlKey = "cloudinary://119264965729773:1qhca12iztxCm0Df0nSBYtsIRF4@bagdash/"; //в конце добавляем '/'
        Image image = new Image();
        File file;
        try{
            file = Files.createTempFile(System.currentTimeMillis() + "",
                    multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length()-4)) // .jpg
                    .toFile();
            multipartFile.transferTo(file);

            Cloudinary cloudinary = new Cloudinary(urlKey);
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            image.setName((String) uploadResult.get("public_id"));
            image.setUrl((String) uploadResult.get("url"));
            image.setFormat((String) uploadResult.get("format"));
            imageRepository.save(image);

            User user = userRepository.findByEmail(userEmail);
            user.setImage(image);
            userRepository.save(user);

            Journal journal = new Journal();
            journal.setTable("USER: " + user.getEmail());
            journal.setAction("set image");
            journal.setUser(user);
            journal.setDeleted(false);
            journalRepository.save(journal);

            return ResponseEntity.ok().body(user);
        }catch (IOException e){
            throw new IOException("User was unable to set a image");
        }
    }

    @Override
    public ResponseMessage deleteImage(String email) {
        User user = userRepository.findByEmail(email);
        user.setImage(null);
        userRepository.save(user);

        Journal journal = new Journal();
        journal.setTable("USER: " + user.getEmail());
        journal.setAction("delete the image");
        journal.setUser(user);
        journal.setDeleted(false);
        journalRepository.save(journal);

        return new ResponseMessage(HttpStatus.OK.value(), "image successfully deleted");
    }

}