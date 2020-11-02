package com.example.fms.service;

import com.example.fms.entity.Account;
import com.example.fms.entity.Journal;
import com.example.fms.entity.User;
import com.example.fms.repository.AccountRepository;
import com.example.fms.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Account create(Account newAccount, String userEmail) {
        Journal journal = new Journal();
        journal.setAction1("ACCOUNT: " + newAccount.getName());
        journal.setAction2("create");
        User user = userService.getByEmail(userEmail);
        journal.setUser(user);
        journalRepository.save(journal);

        return accountRepository.save(newAccount);
    }

    @Override
    public Account getAccountById(Long id) throws Exception {
        return accountRepository.findById(id)
                .orElseThrow(Exception::new);
    }

    @Override
    public Account updateAccountById(Account newAccount, String userEmail) throws Exception{
        Account result = accountRepository.findById(newAccount.getId())
                .map(account -> {
                    account.setName(newAccount.getName());
                    account.setBalance(newAccount.getBalance());
                    return accountRepository.save(account);
                })
                .orElseThrow(Exception::new);

        Journal journal = new Journal();
        journal.setAction1("ACCOUNT: " + newAccount.getName());
        journal.setAction2("update");
        User user = userService.getByEmail(userEmail);
        journal.setUser(user);
        journalRepository.save(journal);

        return result;
    }

    @Override
    public boolean deleteAccountById(Long id, String userEmail) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null){
            accountRepository.deleteById(id);
            Journal journal = new Journal();
            journal.setAction1("ACCOUNT: " + account.getName());
            journal.setAction2("delete");
            User user = userService.getByEmail(userEmail);
            journal.setUser(user);
            journalRepository.save(journal);

            return true;
        }
        return false;
    }
}
