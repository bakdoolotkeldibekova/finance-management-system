package com.example.fms.dto;

import com.example.fms.entity.Account;
import com.example.fms.entity.Category;
import com.example.fms.entity.Counterparty;
import com.example.fms.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionIncomeDTO {
    private Account toAccount;
    private Category category;
    private BigDecimal price;
    private Counterparty counterparty;
    private Project project;
    private String description;
}
