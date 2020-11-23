package com.example.fms;

import com.example.fms.config.SecurityConfig;
import com.example.fms.service.MyUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@ComponentScan(basePackageClasses = {SecurityConfig.class, MyUserDetailsService.class})
class FmsApplicationTest {

    @Test
    void contextLoads() {

    }
}