package com.example.fms.service;

import com.example.fms.entity.Account;
import com.example.fms.entity.User;

import java.util.List;

public interface AccountService {
    List<Account> getAll();
    Account create (Account newAccount, String userEmail);
    Account getAccountById(Long id) throws Exception;
    Account updateAccountById(Account newAccount, String userEmail) throws Exception;
    boolean deleteAccountById(Long id, String userEmail);
}

