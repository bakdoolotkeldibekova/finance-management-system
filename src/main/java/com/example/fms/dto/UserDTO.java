package com.example.fms.dto;

import com.example.fms.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserDTO {
    private String email;
   // private List<Department> departmentList;
}
