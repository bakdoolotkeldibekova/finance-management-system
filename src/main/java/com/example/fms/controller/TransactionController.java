package com.example.fms.controller;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.Role;
import com.example.fms.entity.Transaction;
import com.example.fms.service.TransactionService;
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
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Transaction getById(@PathVariable("id") Long id){
        Transaction transaction =transactionService.getTransactionById(id);
        if (transaction == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
        return transaction;
    }

    @GetMapping("/get")
    public List<Transaction> getAllByParam(HttpServletRequest request, Principal principal) {
        String email = principal.getName();
        boolean isAdmin = false;
        if (userService.getByEmail(email).getRole().getName().equals("ROLE_ADMIN"))
            isAdmin = true;
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

        Set<Transaction> fooSet;
        if (isAdmin)
            fooSet = new LinkedHashSet<>(transactionService.getAllForAdmin());
        else
            fooSet = new LinkedHashSet<>(transactionService.getAllForUser());

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
    public ResponseMessage addIncome (@RequestBody TransactionIncomeDTO transactionIncomeDTO, Principal principal){
        if (userService.getByEmail(principal.getName()) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to add transaction");
        }
        transactionService.addIncome(transactionIncomeDTO, principal.getName());
        return new ResponseMessage(HttpStatus.OK.value(), "Transaction successfully saved");
    }

    @PostMapping("/addExpense")
    public ResponseMessage addExpense (@RequestBody TransactionExpenseDTO transactionExpenseDTO, Principal principal){
        if (userService.getByEmail(principal.getName()) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to add transaction");
        }
        transactionService.addExpense(transactionExpenseDTO, principal.getName());
        return new ResponseMessage(HttpStatus.OK.value(), "Transaction successfully saved");
    }

    @PostMapping("/addRemittance")
    public ResponseMessage addRemittance (@RequestBody TransactionRemittanceDTO transactionRemittanceDTO, Principal principal){
        if (userService.getByEmail(principal.getName()) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to add transaction");
        }
        transactionService.addRemittance(transactionRemittanceDTO, principal.getName());
        return new ResponseMessage(HttpStatus.OK.value(), "Transaction successfully saved");
    }

    @PutMapping("/updateIncome/{id}")
    public ResponseMessage updateIncome (@RequestBody TransactionIncomeDTO newTransaction, @PathVariable Long id, Principal principal) {
        if (userService.getByEmail(principal.getName()) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to update transaction");
        }
        Transaction transaction =transactionService.updateIncomeById(newTransaction, id, principal.getName());
        if (transaction != null)
            return new ResponseMessage(HttpStatus.OK.value(), "Transaction successfully updated");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
    }

    @PutMapping("/updateExpense/{id}")
    public ResponseMessage updateExpense (@RequestBody TransactionExpenseDTO newTransaction, @PathVariable Long id, Principal principal) {
        if (userService.getByEmail(principal.getName()) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to update transaction");
        }
        Transaction transaction = transactionService.updateExpenseById(newTransaction, id, principal.getName());
        if (transaction != null)
            return new ResponseMessage(HttpStatus.OK.value(), "Transaction successfully updated");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
    }

    @PutMapping("/updateRemittance/{id}")
    public ResponseMessage updateRemittance (@RequestBody TransactionRemittanceDTO newTransaction, @PathVariable Long id, Principal principal) {
        if (userService.getByEmail(principal.getName()) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorize to update transaction");
        }
        Transaction transaction = transactionService.updateRemittanceById(newTransaction, id, principal.getName());
        if (transaction != null)
            return new ResponseMessage(HttpStatus.OK.value(), "Transaction successfully updated");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteTransaction (@PathVariable Long id, Principal principal) {
        Transaction transaction = transactionService.deleteTransactionById(id, principal.getName());
        if (transaction != null)
            return new ResponseMessage(HttpStatus.OK.value(), "Transaction successfully deleted");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
    }

}
