package com.example.fms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRemittanceDTO {
    private Long fromAccount;
    private Long toAccount;
    private BigDecimal balance;
    private String description;
}
