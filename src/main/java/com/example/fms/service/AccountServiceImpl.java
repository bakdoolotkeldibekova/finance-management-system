package com.example.fms.service;

import com.example.fms.dto.AccountDTO;
import com.example.fms.entity.Account;
import com.example.fms.entity.Journal;
import com.example.fms.entity.User;
import com.example.fms.repository.AccountRepository;
import com.example.fms.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserService userService;

    @Override
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    @Override
    public List<Account> getAllByName(String name) {
        return accountRepository.findAllByNameContainingIgnoringCase(name);
    }

    @Override
    public List<Account> getAllByBalanceLessThan(BigDecimal balance) {
        return accountRepository.findAllByBalanceLessThanEqual(balance);
    }

    @Override
    public List<Account> getAllByBalanceGreaterThan(BigDecimal balance) {
        return accountRepository.findAllByBalanceGreaterThanEqual(balance);
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public List<Account> getAllByDateCreatedBefore(String before) {
        return accountRepository.findAllByDateCreatedBefore(LocalDateTime.parse(before, formatter));
    }

    @Override
    public List<Account> getAllByDateCreatedAfter(String after) {
        return accountRepository.findAllByDateCreatedAfter(LocalDateTime.parse(after, formatter));
    }

    @Override
    public Account create(AccountDTO accountDTO, String userEmail) {
        Account account = new Account(accountDTO.getName(), accountDTO.getBalance());
        Journal journal = new Journal();
        journal.setTable("ACCOUNT: " + account.getName());
        journal.setAction("create");
        User user = userService.getByEmail(userEmail);
        journal.setUser(user);
        journal.setDeleted(false);
        journalRepository.save(journal);

        return accountRepository.save(account);
    }

    @Override
    public Account getAccountById(Long id) {
         return accountRepository.findById(id).orElse(null);
    }

    @Override
    public Account updateAccountById(AccountDTO accountDTO, Long id, String userEmail){
        Account result = accountRepository.findById(id)
                .map(account -> {
                    account.setName(accountDTO.getName());
                    account.setBalance(accountDTO.getBalance());
                    return accountRepository.save(account);
                })
                .orElse(null);
        if (result != null) {
            Journal journal = new Journal();
            journal.setTable("ACCOUNT: " + accountDTO.getName());
            journal.setAction("update");
            User user = userService.getByEmail(userEmail);
            journal.setUser(user);
            journal.setDeleted(false);
            journalRepository.save(journal);
        }

        return result;
    }

    @Override
    public Account deleteAccountById(Long id, String userEmail) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null){
            accountRepository.deleteById(id);
            Journal journal = new Journal();
            journal.setTable("ACCOUNT: " + account.getName());
            journal.setAction("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
        }
        return account;
    }
}
