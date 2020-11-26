package com.example.fms.service;

import com.example.fms.dto.AccountDTO;
import com.example.fms.entity.Account;
import com.example.fms.entity.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    List<Account> getAll(boolean isDeleted);
    Page<Account> getByPage(List<Account> list, Pageable pageable);
    List<Account> getAllByName(String name);
    List<Account> getAllByBalanceLessThan(BigDecimal balance);
    List<Account> getAllByBalanceGreaterThan(BigDecimal balance);
    List<Account> getAllByDateCreatedBefore(String before);
    List<Account> getAllByDateCreatedAfter(String after);
    ResponseEntity<Account> create (AccountDTO accountDTO, String userEmail);
    ResponseEntity<Account> getAccountById(Long id);
    Account getById(Long id);
    ResponseEntity<Account> updateAccountById(AccountDTO accountDTO, Long id, String userEmail);
    ResponseMessage deleteAccountById(Long id, String userEmail);
}

