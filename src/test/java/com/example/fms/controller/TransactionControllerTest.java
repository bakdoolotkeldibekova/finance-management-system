package com.example.fms.controller;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
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
class TransactionControllerTest {

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
                .perform(get("/transaction/get")
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void getById() throws Exception{
        MvcResult result = mvc
                .perform(get("/transaction/{id}", 1)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void getById_returnsError() throws Exception{
        MvcResult result = mvc
                .perform(get("/transaction/{id}", 0)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }



    @Test
    void addIncome() throws Exception{
        TransactionIncomeDTO income = new TransactionIncomeDTO(1L, 1L, BigDecimal.valueOf(120), 1L, 1L, "incomeTest", 1L);
        String jsonRequest = mapper.writeValueAsString(income);

        MvcResult result = mvc
                .perform(post("/transaction/addIncome")
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void addExpense() throws Exception{
        TransactionExpenseDTO expense = new TransactionExpenseDTO(1L, 1L, BigDecimal.valueOf(120), 1L, 1L, "expense Test", 1L);
        String jsonRequest = mapper.writeValueAsString(expense);

        MvcResult result = mvc
                .perform(post("/transaction/addExpense")
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void addRemittance() throws Exception{
        TransactionRemittanceDTO remittance = new
                TransactionRemittanceDTO(2L, 2L, BigDecimal.valueOf(10), "remittance Test");
        String jsonRequest = mapper.writeValueAsString(remittance);

        MvcResult result = mvc
                .perform(post("/transaction/addRemittance")
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void updateIncome() throws Exception{
        TransactionIncomeDTO income = new TransactionIncomeDTO(1L, 1L, BigDecimal.valueOf(120), 1L, 1L, "update income Test", 1L);
        String jsonRequest = mapper.writeValueAsString(income);

        MvcResult result = mvc
                .perform(put("/transaction/updateIncome/{id}", 1)
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void updateIncome_returnsError() throws Exception{
        TransactionIncomeDTO income = new TransactionIncomeDTO(1L, 1L, BigDecimal.valueOf(120), 1L, 1L, "update income Test", 1L);
        String jsonRequest = mapper.writeValueAsString(income);

        MvcResult result = mvc
                .perform(put("/transaction/updateIncome/{id}", 0)
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }

    @Test
    void updateExpense() throws Exception{
        TransactionExpenseDTO expense = new TransactionExpenseDTO(1L, 1L, BigDecimal.valueOf(120), 1L, 1L, "update expense Test", 1L);
        String jsonRequest = mapper.writeValueAsString(expense);

        MvcResult result = mvc
                .perform(put("/transaction/updateExpense/{id}", 2)
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void updateExpense_returnsError() throws Exception{
        TransactionExpenseDTO expense = new TransactionExpenseDTO(1L, 1L, BigDecimal.valueOf(120), 1L, 1L, "update expense Test", 1L);
        String jsonRequest = mapper.writeValueAsString(expense);

        MvcResult result = mvc
                .perform(put("/transaction/updateExpense/{id}", 0)
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }

    @Test
    void updateRemittance() throws Exception{
        TransactionRemittanceDTO remittance = new
                TransactionRemittanceDTO(2L, 2L, BigDecimal.valueOf(10), "update remittance Test");
        String jsonRequest = mapper.writeValueAsString(remittance);

        MvcResult result = mvc
                .perform(put("/transaction/updateRemittance/{id}", 5)
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void updateRemittance_returnsError() throws Exception{
        TransactionRemittanceDTO remittance = new TransactionRemittanceDTO(1L, 1L, BigDecimal.valueOf(120), "remittance Test");
        String jsonRequest = mapper.writeValueAsString(remittance);

        MvcResult result = mvc
                .perform(put("/transaction/updateRemittance/{id}", 0)
                        .content(jsonRequest)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }

    @Test
    void deleteTransaction() throws Exception{
        MvcResult result = mvc
                .perform(delete("/transaction/{id}", 1)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void deleteTransaction_returnsError() throws Exception{
        MvcResult result = mvc
                .perform(delete("/transaction/{id}", 0)
                        .header("Authorization", createToken(claims, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }
}