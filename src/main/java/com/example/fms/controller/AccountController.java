package com.example.fms.controller;

import com.example.fms.dto.AccountDTO;
import com.example.fms.entity.Account;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.User;
import com.example.fms.service.AccountService;
import com.example.fms.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final UserService userService;

    AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping("/get")
    public List<Account> getAllByParam(HttpServletRequest request, Principal principal){
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get account information");
        }
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
    public Account getAccount(@PathVariable("accountId") Long accountId, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to get account information");
        }
        Account account = accountService.getAccountById(accountId);
        if (account != null) {
            return account;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + accountId + " not found!");
    }

    @PostMapping("/add")
    public ResponseMessage add(@RequestBody AccountDTO accountDTO, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to add account");
        }
        accountService.create(accountDTO, principal.getName());
        return new ResponseMessage(HttpStatus.OK.value(), "Account successfully saved");
    }

    @PutMapping("/update/{id}")
    public ResponseMessage updateAccount(@RequestBody AccountDTO accountDTO, @PathVariable Long id, Principal principal){
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to change account information");
        }

        Account account = accountService.updateAccountById(accountDTO, id, principal.getName());
        if (account != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Account successfully updated");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + id + " not found!");
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteAccount(@PathVariable("id") Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to delete account information");
        }

        Account account = accountService.deleteAccountById(id, principal.getName());
        if (account != null) {
            return new ResponseMessage(HttpStatus.OK.value(), "Account successfully deleted");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + id + " not found!");
    }

}
