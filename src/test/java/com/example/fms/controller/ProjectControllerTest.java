package com.example.fms.controller;

import com.example.fms.entity.Project;
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
class ProjectControllerTest {

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
    void getAllByParam() throws Exception{
        MvcResult result = mvc
                .perform(get("/project/get")
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void getById() throws Exception{
        MvcResult result = mvc
                .perform(get("/project/{id}", 2)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    void getById_returnsError() throws Exception{
        MvcResult result = mvc
                .perform(get("/project/{id}", 0)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());

    }

    @Test
    void addProject() throws Exception{
        Project project = new Project("FMS-TEAM4");
        String jsonRequest = mapper.writeValueAsString(project);

        MvcResult result = mvc
                .perform(post("/project/add")
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void updateProject() throws Exception{
        Project project = new Project("FMS-TEAM4");
        String jsonRequest = mapper.writeValueAsString(project);

        MvcResult result = mvc
                .perform(put("/project/update/{id}", 2)
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void updateProject_returnsError() throws Exception{
        Project project = new Project("FMS-TEAM4");
        String jsonRequest = mapper.writeValueAsString(project);

        MvcResult result = mvc
                .perform(put("/project/update/{id}", 0)
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }

    @Test
    void deleteProject() throws Exception{
        MvcResult result = mvc
                .perform(delete("/project/{id}", 1)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void deleteProject_returnsError() throws Exception{
        MvcResult result = mvc
                .perform(delete("/project/{id}", 0)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }
}