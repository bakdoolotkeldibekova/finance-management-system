package com.example.fms.controller;

import com.example.fms.entity.Account;
import com.example.fms.service.AccountService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/get")
    public List<Account> getAllByParam(HttpServletRequest request){
        String name = request.getParameter("name");
        String balanceLessThan = request.getParameter("balanceLessThan");
        String balanceGreaterThan = request.getParameter("balanceGreaterThan");
        String dateAfter = request.getParameter("dateAfter");
        String dateBefore = request.getParameter("dateBefore");

        Set<Account> fooSet = new LinkedHashSet<>(accountService.getAll());

        if (name != null)
            fooSet.retainAll(accountService.getAllByName(name));
        if (balanceLessThan != null)
            fooSet.retainAll(accountService.getAllByBalanceLessThan(new BigDecimal(balanceLessThan)));
        if (balanceGreaterThan != null)
            fooSet.retainAll(accountService.getAllByBalanceGreaterThan(new BigDecimal(balanceGreaterThan)));
        if (dateAfter != null)
            fooSet.retainAll(accountService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(accountService.getAllByDateCreatedBefore(dateBefore));

        return new ArrayList<>(fooSet);
    }

    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable("accountId") Long accountId) throws Exception{
        return accountService.getAccountById(accountId);
    }

    @PostMapping("/add")
    public Account add (@RequestBody Account newAccount, Principal principal) {
        return accountService.create(newAccount, principal.getName());
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
