package com.example.fms.dto;

import com.example.fms.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserAdminDTO {
    private String email;
    private String name;
    private String surname;
    private String password;
    private String position;
}
