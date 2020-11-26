package com.example.fms.controller;

import com.example.fms.dto.AccountDTO;
import com.example.fms.entity.Account;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.exception.NotEnoughBalanceException;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.service.AccountService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;
    AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @GetMapping("/get")
    public Page<Account> getAllByParam(Pageable pageable,
                                       @RequestParam(value = "isDeleted", required = false, defaultValue = "false") boolean isDeleted,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) BigDecimal balanceLessThan,
                                       @RequestParam(required = false) BigDecimal balanceGreaterThan,
                                       @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                       @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore){
       Set<Account> fooSet = new LinkedHashSet<>(accountService.getAll(isDeleted));

        if (name != null)
            fooSet.retainAll(accountService.getAllByName(name));
        if (balanceLessThan != null)
            fooSet.retainAll(accountService.getAllByBalanceLessThan(balanceLessThan));
        if (balanceGreaterThan != null)
            fooSet.retainAll(accountService.getAllByBalanceGreaterThan(balanceGreaterThan));
        if (dateAfter != null)
            fooSet.retainAll(accountService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(accountService.getAllByDateCreatedBefore(dateBefore));
        List<Account> list = new ArrayList<>(fooSet);
        return accountService.getByPage(list, pageable);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getById(@PathVariable("accountId") @Min(1) Long accountId) {

        return accountService.getAccountById(accountId);
    }

    @PostMapping("/add")
    public ResponseEntity<Account> create(@RequestBody AccountDTO accountDTO, Principal principal) {
        return accountService.create(accountDTO, principal.getName());
      }

    @PutMapping("/update/{id}")
    public ResponseEntity<Account> updateAccount(@RequestBody AccountDTO accountDTO, @PathVariable Long id, Principal principal){
      return accountService.updateAccountById(accountDTO, id, principal.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteAccount(@PathVariable("id") Long id, Principal principal) {
        return accountService.deleteAccountById(id, principal.getName());
       }
}
