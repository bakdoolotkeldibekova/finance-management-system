package com.example.fms.controller;

import com.example.fms.dto.UserAuthDTO;
import com.example.fms.dto.UserRegistrDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@WebAppConfiguration
class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    String secret = "neobis";
    Map<String, Object> claims = new HashMap<>();
    String username = "1804.01026@manas.edu.kg";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultRequest(get("/").with(user("user").roles("ADMIN")))
                .apply(springSecurity())
                .build();
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 48 * 60 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    @Test
    void activate() throws Exception{
        String code = "d2d6f43b-e0e3-4840-b56a-6b0c9aa61303";

        MvcResult result = mvc
                .perform(get("/registr/activate/{code}", code)
                        //.header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    void saveUser() throws Exception{
        UserRegistrDTO user = new UserRegistrDTO("1804.01026@manas.edu.kg", "Zhazgul", "K", "12345678");
        String jsonRequest = mapper.writeValueAsString(user);

        MvcResult result = mvc
                .perform(post("/registr")
                        //.header("Authorization", createToken(claims, username))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void changePassword() throws Exception{
        UserAuthDTO user = new UserAuthDTO("1804.01026@manas.edu.kg", "12345678");
        String jsonRequest = mapper.writeValueAsString(user);

        MvcResult result = mvc
                .perform(put("/registr/changePassword")
                        //.header("Authorization", createToken(claims, username))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void sendForgotPassword() throws Exception{
        String email = "1804.01026@manas.edu.kg";
        MvcResult result = mvc
                .perform(post("/registr/forgotPassword/{email}", email)
                        //.header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void sayHello() throws Exception{
        MvcResult result = mvc
                .perform(get("/registr/hello")
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void getToken() throws Exception{
        UserAuthDTO user = new UserAuthDTO("1804.01026@manas.edu.kg", "12345678");
        String jsonRequest = mapper.writeValueAsString(user);

        MvcResult result = mvc
                .perform(post("/registr/auth")
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());

    }
}