package com.example.fms.controller;

import com.example.fms.dto.UserAuthDTO;
import com.example.fms.dto.UserDTO;
import com.example.fms.dto.UserRegistrDTO;
import com.example.fms.service.UserService;
import com.example.fms.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/registr")
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public boolean createUser(@RequestBody UserDTO userDTO){
        return userService.createUser(userDTO);
    }

    @GetMapping("/activate/{code}")
    public String  activate(@PathVariable String code) {
        return userService.activateUser(code);
    }

    @PostMapping
    public boolean saveUser(@RequestBody UserRegistrDTO userRegistrDTO){
        return userService.save(userRegistrDTO);
    }

    @PutMapping("/changePassword")
    public boolean changePassword(@RequestBody UserAuthDTO userAuthDTO){
        return userService.changePassword(userAuthDTO.getEmail(), userAuthDTO.getPassword());
    }

    @PostMapping("/forgotPassword") //на email юзера приходит уникальная ссылка со сроком истечения в 5 минут для изменения пароля на почту.
    public boolean sendForgotPassword(@RequestBody String email){
        return userService.sendForgotPassword(email);
    }

    @GetMapping("/hello")//proverka
    public String sayHello(Principal principal) {
        return "Hello, " + principal.getName();
    }

    @PostMapping("/auth")
    public String getToken(@RequestBody UserAuthDTO userAuthDTO) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userAuthDTO.getEmail(), userAuthDTO.getPassword()));
        } catch (Exception e){
            throw new Exception("Auth failed");
        }
        return jwtUtil.generateToken(userAuthDTO.getEmail());
    }
}

