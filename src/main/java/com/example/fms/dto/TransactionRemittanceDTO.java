package com.example.fms.dto;

import com.example.fms.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRemittanceDTO {
    private Account fromAccount;
    private Account toAccount;
    private BigDecimal balance;
    private String description;
}
