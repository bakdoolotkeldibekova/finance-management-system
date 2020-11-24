package com.example.fms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionIncomeDTO {
    private Long toAccount;
    private Long category;
    private BigDecimal balance;
    private Long counterparty;
    private Long project;
    private String description;
}
