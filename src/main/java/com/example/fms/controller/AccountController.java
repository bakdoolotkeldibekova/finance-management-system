package com.example.fms.controller;

import com.example.fms.entity.Account;
import com.example.fms.entity.User;
import com.example.fms.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Aggregate root
    @GetMapping("/getAll")
    public List<Account> getAll() {
        return (List<Account>) accountService.getAll();
    }

    @PostMapping("/add")
    public Account add (@RequestBody Account newAccount, Principal principal) {
        return accountService.create(newAccount, principal.getName());
    }

    //single item

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable("id") Long id) throws Exception {
        return accountService.getAccountById(id);
    }

    @PutMapping("/update")
    public Account updateAccount(@RequestBody Account newAccount, Principal principal) throws Exception{
        return accountService.updateAccountById(newAccount, principal.getName());
    }

    @DeleteMapping("/delete/{id}")
    public void deleteAccount(@PathVariable("id") Long id, Principal principal) {
        accountService.deleteAccountById(id, principal.getName());
    }

}
