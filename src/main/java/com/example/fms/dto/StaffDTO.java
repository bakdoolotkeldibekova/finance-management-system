package com.example.fms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class StaffDTO {
    private String name;
    private List<Long> departments;
    private String position;
    private BigDecimal salary;
    private LocalDateTime date;    //Дата начала работы сотрудника
    private BigDecimal accepted;
}
