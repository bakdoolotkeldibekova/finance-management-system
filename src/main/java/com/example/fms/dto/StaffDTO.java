package com.example.fms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class StaffDTO {
    private Long id;
    private String name;
    private List<Long> departments;
    private String position;
    private BigDecimal salary;
    private Locale date;    //Дата начала работы сотрудника
    private BigDecimal accepted;
}
