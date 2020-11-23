package com.example.fms.service;

import com.example.fms.dto.AccountDTO;
import com.example.fms.entity.*;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.AccountRepository;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.hibernate.Filter;
import org.hibernate.Session;

import javax.persistence.EntityManager;
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
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Account> getAll(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedAccountFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<Account> accounts = accountRepository.findAll();
        session.disableFilter("deletedAccountFilter");
        return accounts;
    }

    @Override
    public Page<Account> getByPage(List<Account> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<Account>(list.subList(start, end), pageable, list.size());
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
    public ResponseEntity<Account> create(AccountDTO accountDTO, String userEmail) {
        Account account = new Account(accountDTO.getName(), accountDTO.getBalance());
        accountRepository.save(account);

        Journal journal = new Journal();
        journal.setTable("ACCOUNT: " + account.getName());
        journal.setAction("create");
        User user = userRepository.findByEmail(userEmail);
        journal.setUser(user);
        journal.setDeleted(false);
        journalRepository.save(journal);

        return ResponseEntity.ok().body(account);
    }

    @Override
    public ResponseEntity<Account> getAccountById(Long id) {
         Account account = accountRepository.findById(id)
                 .orElseThrow(()->new ResourceNotFoundException("Account id " + id + " not found!"));
         if (account.isDeleted())
             throw new ResourceNotFoundException("Account id " + id + " was deleted!");
         return ResponseEntity.ok().body(account);
    }

    @Override
    public ResponseEntity<Account> updateAccountById(AccountDTO accountDTO, Long id, String userEmail){
        Account result = accountRepository.findById(id)
                .map(account -> {
                    if (account.isDeleted())
                        throw new ResourceNotFoundException("Account id " + id + " was deleted!");
                    account.setName(accountDTO.getName());
                    account.setBalance(accountDTO.getBalance());
                    return accountRepository.save(account);
                })
                .orElseThrow(()->new ResourceNotFoundException("Account id " + id + " not found!"));
            //на нулл не нужно проверять
            Journal journal = new Journal();
            journal.setTable("ACCOUNT: " + accountDTO.getName());
            journal.setAction("update");
            User user = userRepository.findByEmail(userEmail);
            journal.setUser(user);
            journal.setDeleted(false);
            journalRepository.save(journal);

        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseMessage deleteAccountById(Long id, String userEmail) {
        Account account = accountRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Account id " + id + " not found!"));

        if (account.isDeleted())
            throw new ResourceNotFoundException("Account id " + id + " was deleted!");

        accountRepository.deleteById(id);

        Journal journal = new Journal();
        journal.setTable("ACCOUNT: " + account.getName());
        journal.setAction("delete");
        journal.setUser(userRepository.findByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return new ResponseMessage(HttpStatus.OK.value(), "Account successfully deleted");
    }
}
