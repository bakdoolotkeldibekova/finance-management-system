package com.example.fms.controller;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.Transaction;
import com.example.fms.repository.UserRepository;
import com.example.fms.service.TransactionService;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable("id") Long id, Principal principal){
        String email = principal.getName();
        if (userRepository.findByEmail(email).getRole().getName().equals("ROLE_ADMIN"))
            return transactionService.getByIdForAdmin(id);
        else return transactionService.getByIdForUser(id);
    }

    @GetMapping("/get")
    public Page<Transaction> getAllByParam(Pageable pageable,
                                           @RequestParam(required = false) String action,
                                           @RequestParam(required = false) Long fromAccountId,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) Long toAccountId,
                                           @RequestParam(required = false) BigDecimal balanceLessThan,
                                           @RequestParam(required = false) BigDecimal balanceGreaterThan,
                                           @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) Long projectId,
                                           @RequestParam(required = false) Long counterpartyId,
                                           @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                           @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore, Principal principal) {
        Set<Transaction> fooSet;
        String email = principal.getName();
        if (userRepository.findByEmail(email).getRole().getName().equals("ROLE_ADMIN"))
            fooSet = new LinkedHashSet<>(transactionService.getAllForAdmin());
        else
            fooSet = new LinkedHashSet<>(transactionService.getAllForUser());

        if (action != null)
            fooSet.retainAll(transactionService.getAllByAction(action));
        if (fromAccountId != null)
            fooSet.retainAll(transactionService.getAllByFromAccount(fromAccountId));
        if (categoryId != null)
            fooSet.retainAll(transactionService.getAllByCategory(categoryId));
        if (toAccountId != null)
            fooSet.retainAll(transactionService.getAllByToAccount(toAccountId));
        if (balanceLessThan != null)
            fooSet.retainAll(transactionService.getAllByBalanceLessThanEqual(balanceLessThan));
        if (balanceGreaterThan != null)
            fooSet.retainAll(transactionService.getAllByBalanceGreaterThanEqual(balanceGreaterThan));
        if (userId != null)
            fooSet.retainAll(transactionService.getAllByUserId(userId));
        if (projectId != null)
            fooSet.retainAll(transactionService.getAllByProject(projectId));
        if (counterpartyId != null)
            fooSet.retainAll(transactionService.getAllByCounterparty(counterpartyId));
        if (dateAfter != null)
            fooSet.retainAll(transactionService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(transactionService.getAllByDateCreatedBefore(dateBefore));

        List<Transaction> list = new ArrayList<>(fooSet);
        return transactionService.getByPage(list, pageable);
    }

    @PostMapping("/addIncome")
    public ResponseEntity<Transaction> addIncome (@RequestBody TransactionIncomeDTO transactionIncomeDTO, Principal principal){
        return transactionService.addIncome(transactionIncomeDTO, principal.getName());
    }

    @PostMapping("/addExpense")
    public ResponseEntity<Transaction> addExpense (@RequestBody TransactionExpenseDTO transactionExpenseDTO, Principal principal){
       return transactionService.addExpense(transactionExpenseDTO, principal.getName());
    }

    @PostMapping("/addRemittance")
    public ResponseEntity<Transaction> addRemittance (@RequestBody TransactionRemittanceDTO transactionRemittanceDTO, Principal principal){
        return transactionService.addRemittance(transactionRemittanceDTO, principal.getName());
    }

//    @PostMapping("/addIncome")
//    public ResponseEntity<Transaction> addIncome (@RequestBody TransactionIncomeDTO transactionIncomeDTO, Principal principal){
//        return transactionService.addTransaction(transactionIncomeDTO, principal.getName());
//    }
//
//    @PostMapping("/addExpense")
//    public ResponseEntity<Transaction> addExpense (@RequestBody TransactionExpenseDTO transactionExpenseDTO, Principal principal){
//       return transactionService.addTransaction(transactionExpenseDTO, principal.getName());
//    }
//
//    @PostMapping("/addRemittance")
//    public ResponseEntity<Transaction> addRemittance (@RequestBody TransactionRemittanceDTO transactionRemittanceDTO, Principal principal){
//        return transactionService.addTransaction(transactionRemittanceDTO, principal.getName());
//    }

    @PutMapping("/updateIncome/{id}")
    public ResponseEntity<Transaction> updateIncome (@RequestBody TransactionIncomeDTO newTransaction, @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        if (userRepository.findByEmail(email).getRole().getName().equals("ROLE_USER")) {
            Transaction transaction = transactionService.getByIdForUser(id).getBody();
            if (transaction == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
        }
        return transactionService.updateIncomeById(newTransaction, id, principal.getName());
    }

    @PutMapping("/updateExpense/{id}")
    public ResponseEntity<Transaction> updateExpense (@RequestBody TransactionExpenseDTO newTransaction, @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        if (userRepository.findByEmail(email).getRole().getName().equals("ROLE_USER")) {
            Transaction transaction = transactionService.getByIdForUser(id).getBody();
            if (transaction == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
        }
        return transactionService.updateExpenseById(newTransaction, id, principal.getName());
    }

    @PutMapping("/updateRemittance/{id}")
    public ResponseEntity<Transaction> updateRemittance (@RequestBody TransactionRemittanceDTO newTransaction, @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        if (userRepository.findByEmail(email).getRole().getName().equals("ROLE_USER")) {
            Transaction transaction = transactionService.getByIdForUser(id).getBody();
            if (transaction == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
        }
        return transactionService.updateRemittanceById(newTransaction, id, principal.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseMessage deleteTransaction (@PathVariable Long id, Principal principal) {
        String email = principal.getName();
        if (userRepository.findByEmail(email).getRole().getName().equals("ROLE_USER")) {
            Transaction transaction = transactionService.getByIdForUser(id).getBody();
            if (transaction == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
        }
        return transactionService.deleteTransactionById(id, principal.getName());
    }

}
