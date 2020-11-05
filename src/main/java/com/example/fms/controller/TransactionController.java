package com.example.fms.controller;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.Transaction;
import com.example.fms.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    public Transaction getById(@PathVariable("id") Long id) throws Exception{
        return transactionService.getTransactionById(id);
    }

    @GetMapping("/get")
    public List<Transaction> getAllByParam(HttpServletRequest request) {
        String action = request.getParameter("action");
        String fromAccount = request.getParameter("fromAccount");
        String category = request.getParameter("category");
        String toAccount = request.getParameter("toAccount");
        String balanceLessThan = request.getParameter("balanceLessThan");
        String balanceGreaterThan = request.getParameter("balanceGreaterThan");
        String user = request.getParameter("user");
        String project = request.getParameter("project");
        String counterparty = request.getParameter("counterparty");
        String dateAfter = request.getParameter("dateAfter");
        String dateBefore = request.getParameter("dateBefore");

        Set<Transaction> fooSet = new LinkedHashSet<>(transactionService.getAll());

        if (action != null)
            fooSet.retainAll(transactionService.getAllByAction(action));
        if (fromAccount != null)
            fooSet.retainAll(transactionService.getAllByFromAccount(Long.parseLong(fromAccount)));
        if (category != null)
            fooSet.retainAll(transactionService.getAllByCategory(Long.parseLong(category)));
        if (toAccount != null)
            fooSet.retainAll(transactionService.getAllByToAccount(Long.parseLong(toAccount)));
        if (balanceLessThan != null)
            fooSet.retainAll(transactionService.getAllByBalanceLessThanEqual(new BigDecimal(balanceLessThan)));
        if (balanceGreaterThan != null)
            fooSet.retainAll(transactionService.getAllByBalanceGreaterThanEqual(new BigDecimal(balanceGreaterThan)));
        if (user != null)
            fooSet.retainAll(transactionService.getAllByUserId(Long.parseLong(user)));
        if (project != null)
            fooSet.retainAll(transactionService.getAllByProject(Long.parseLong(project)));
        if (counterparty != null)
            fooSet.retainAll(transactionService.getAllByCounterparty(Long.parseLong(counterparty)));
        if (dateAfter != null)
            fooSet.retainAll(transactionService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(transactionService.getAllByDateCreatedBefore(dateBefore));

        return new ArrayList<>(fooSet);
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

    @PutMapping("/updateIncome")
    public Transaction updateIncome (@RequestBody TransactionIncomeDTO newTransaction, Principal principal) throws Exception{
        return transactionService.updateIncomeById(newTransaction, principal.getName());
    }

    @PutMapping("/updateExpense")
    public Transaction updateExpense (@RequestBody TransactionExpenseDTO newTransaction, Principal principal) throws Exception{
        return transactionService.updateExpenseById(newTransaction, principal.getName());
    }

    @PutMapping("/updateRemittance")
    public Transaction updateRemittance (@RequestBody TransactionRemittanceDTO newTransaction, Principal principal) throws Exception{
        return transactionService.updateRemittanceById(newTransaction, principal.getName());
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteTransaction (@PathVariable Long id, Principal principal) {
        return transactionService.deleteTransactionById(id, principal.getName());
    }
}
