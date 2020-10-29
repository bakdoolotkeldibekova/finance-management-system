package com.example.fms.controller;

import com.example.fms.entity.User;
import com.example.fms.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/position")
    public User setPosition(@RequestBody String position, Principal principal){
        return userService.setPosition(position, principal.getName());
    }

    @DeleteMapping("/{id}")
    public boolean deleteByd(@PathVariable Long id){
        return userService.deleteUserById(id);
    }

    @GetMapping
    public List<User> getAll(){
        return userService.getAll();
    }

    @GetMapping("/active")
    public List<User> getAllByActive(@RequestBody boolean isActive){
        return userService.getAllByActive(isActive);
    }

    @GetMapping("/name")
    public List<User> getAllByNameOrSurname(@RequestBody String nameOrSurname){
        return userService.getAllByNameOrSurname(nameOrSurname);
    }

    @GetMapping("/date/{after}/{before}")
    public List<User> getAllByDateCreatedBetween(@PathVariable String after, @PathVariable String before){
        return userService.getAllByDateCreatedBetween(after, before);
    }

    @GetMapping("/position")
    public List<User> getAllByPosition(@RequestBody String position){
        return userService.getAllByPosition(position);
    }

    @GetMapping("/email")
    public User getByEmail(@RequestBody String email){ //email нужно написать точно и правильно
        return userService.getByEmail(email);
    }

}
