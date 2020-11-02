package com.example.fms.controller;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.Transaction;
import com.example.fms.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/getAll")
    public List<Transaction> getAll() {
        return transactionService.getAll();
    }

    @PostMapping("/addIncome")
    public Transaction addIncome (@RequestBody TransactionIncomeDTO transactionIncomeDTO, Principal principal) throws Exception{
        return transactionService.addIncome(transactionIncomeDTO, principal.getName());
    }

    @PostMapping("/addExpense")
    public Transaction addExpense (@RequestBody TransactionExpenseDTO transactionExpenseDTO, Principal principal) throws Exception{
        return transactionService.addExpense(transactionExpenseDTO, principal.getName());
    }

    @PostMapping("/addRemittance")
    public Transaction addRemittance (@RequestBody TransactionRemittanceDTO transactionRemittanceDTO, Principal principal) throws Exception{
        return transactionService.addRemittance(transactionRemittanceDTO, principal.getName());
    }

    @GetMapping("/getById/{id}")
    public Transaction getTransaction (@PathVariable Long id) throws Exception{
        return transactionService.getTransactionById(id);
    }

    @GetMapping("/getByAction/{action}")
    public List<Transaction> getByAction(@PathVariable("action") String action) {
        return transactionService.getByAction(action);
    }

    @GetMapping("/getByUser/{userId}")
    public List<Transaction> getByUser(@PathVariable("userId") Long userId) {
        return transactionService.getByUserId(userId);
    }

    @GetMapping("/getByAccount/{accountId}")
    public List<Transaction> getByAccount(@PathVariable("accountId") Long accountId) {
        return transactionService.getByAccountId(accountId);
    }

    @GetMapping("/getByDate/{after}/{before}")
    public List<Transaction> getByDate(@PathVariable("after") String after, @PathVariable("before") String before) {
        return transactionService.getByDate(after, before);
    }

    @GetMapping("/getByCategory/{categoryId}")
    public List<Transaction> getByCategory(@PathVariable("categoryId") Long categoryId) {
        return transactionService.getByCategory(categoryId);
    }

    @PutMapping("/update")
    public Transaction updateTransaction (@RequestBody Transaction newTransaction, Principal principal) throws Exception{
        return transactionService.updateTransactionById(newTransaction, principal.getName());
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteTransaction (@PathVariable Long id, Principal principal) {
        return transactionService.deleteTransactionById(id, principal.getName());
    }
}
