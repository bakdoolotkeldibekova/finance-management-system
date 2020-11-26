package com.example.fms.controller;

import com.example.fms.dto.StaffDTO;
import com.example.fms.entity.Department;
import com.example.fms.entity.Staff;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@WebAppConfiguration
class StaffControllerTest {

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
                .perform(get("/staff/get")
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void getById() throws Exception{
        MvcResult result = mvc
                .perform(get("/staff/{id}", 1)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    void getById_returnsError() throws Exception{
        MvcResult result = mvc
                .perform(get("/staff/{id}", 0)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());

    }

    @Test
    void addStaff() throws Exception{
        List<Long> departments = new ArrayList<>();
        departments.add(1L);
        LocalDateTime date = LocalDateTime.parse("2020-10-10 18:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        StaffDTO staff = new StaffDTO("Bulut", departments, "student", BigDecimal.valueOf(200), date, BigDecimal.valueOf(200));
        String jsonRequest = mapper.writeValueAsString(staff);

        MvcResult result = mvc
                .perform(post("/staff/add")
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void updateStaff() throws Exception{
        List<Long> departments = new ArrayList<>();
        departments.add(1L);
        LocalDateTime date = LocalDateTime.parse("2020-10-10 18:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        StaffDTO staff = new StaffDTO("Bulut", departments, "student", BigDecimal.valueOf(200), date, BigDecimal.valueOf(200));
        String jsonRequest = mapper.writeValueAsString(staff);

        MvcResult result = mvc
                .perform(put("/staff/update/{id}", 1)
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void updateStaff_returnsError() throws Exception{
        List<Long> departments = new ArrayList<>();
        departments.add(1L);
        LocalDateTime date = LocalDateTime.parse("2020-10-10 18:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        StaffDTO staff = new StaffDTO("Bulut", departments, "student", BigDecimal.valueOf(200), date, BigDecimal.valueOf(200));
        String jsonRequest = mapper.writeValueAsString(staff);

        MvcResult result = mvc
                .perform(put("/staff/update/{id}", 0)
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }

    @Test
    void deleteStaff() throws Exception{
        MvcResult result = mvc
                .perform(delete("/staff/{id}", 1)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void deleteStaff_returnsError() throws Exception{
        MvcResult result = mvc
                .perform(delete("/staff/{id}", 0)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }
}