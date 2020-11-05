package com.example.fms.service;

import com.example.fms.entity.Account;
import com.example.fms.entity.User;

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
    Account create (Account newAccount, String userEmail);
    Account getAccountById(Long id) throws Exception;
    Account updateAccountById(Account newAccount, String userEmail) throws Exception;
    boolean deleteAccountById(Long id, String userEmail);
}

