package com.example.fms.service;

import com.example.fms.dto.AccountDTO;
import com.example.fms.entity.Account;
import com.example.fms.entity.User;
import javassist.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface AccountService {
    List<Account> getAll();
    List<Account> getAllByName(String name);
    List<Account> getAllByBalanceLessThan(BigDecimal balance);
    List<Account> getAllByBalanceGreaterThan(BigDecimal balance);
    List<Account> getAllByDateCreatedBefore(String before);
    List<Account> getAllByDateCreatedAfter(String after);
    Account create (AccountDTO accountDTO, String userEmail);
    Account getAccountById(Long id);
    Account updateAccountById(AccountDTO accountDTO, Long id, String userEmail);
    Account deleteAccountById(Long id, String userEmail);
}

