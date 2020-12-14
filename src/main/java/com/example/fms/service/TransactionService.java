package com.example.fms.service;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TransactionService {

    List<Transaction> getAll(boolean isDeleted, String email);
    Page<Transaction> getByPage(List<Transaction> list, Pageable pageable);

    ResponseEntity<Transaction> addIncome(TransactionIncomeDTO transactionIncomeDTO, String userEmail);
    ResponseEntity<Transaction> addExpense(TransactionExpenseDTO transactionExpenseDTO, String userEmail);
    ResponseEntity<Transaction> addRemittance(TransactionRemittanceDTO transactionRemittanceDTO, String userEmail);

    ResponseEntity<Transaction> getByIdForUser (Long id, String userEmail);
    ResponseEntity<Transaction> getByIdForAdmin (Long id);

    List<Transaction> getAllByAction(String action);

    List<Transaction> getAllByUserId(Long userId);

    List<Transaction> getAllByFromAccount(Long accountId);

    List<Transaction> getAllByToAccount(Long accountId);

    List<Transaction> getAllByCategory(Long categoryId);

    List<Transaction> getAllByBalanceGreaterThanEqual(BigDecimal balance);
    List<Transaction> getAllByBalanceLessThanEqual(BigDecimal balance);
    List<Transaction> getAllByDateCreatedAfter(String after);
    List<Transaction> getAllByDateCreatedBefore(String before);
    List<Transaction> getAllByProject(Long projectId);
    List<Transaction> getAllByCounterparty(Long counterpartyId);

    ResponseEntity<Transaction> updateIncomeById(TransactionIncomeDTO newTransaction, Long id, String userEmail);
    ResponseEntity<Transaction> updateExpenseById(TransactionExpenseDTO newTransaction, Long id, String userEmail);
    ResponseEntity<Transaction> updateRemittanceById(TransactionRemittanceDTO newTransaction, Long id, String userEmail);

    ResponseMessage deleteTransactionById (Long id, String userEmail);
    ResponseEntity<Map<String,BigDecimal>> incomeCategory(LocalDateTime after, LocalDateTime before);
    ResponseEntity<Map<String,BigDecimal>> incomeProject(LocalDateTime after, LocalDateTime before);
    ResponseEntity<Map<String,BigDecimal>> incomeCounterparty(LocalDateTime after, LocalDateTime before);

    ResponseEntity<Map<String,BigDecimal>> expenseCategory(LocalDateTime after, LocalDateTime before);
    ResponseEntity<Map<String,BigDecimal>> expenseProject(LocalDateTime after, LocalDateTime before);
    ResponseEntity<Map<String,BigDecimal>> expenseCounterparty(LocalDateTime after, LocalDateTime before);
    ResponseEntity<BigDecimal> profit(LocalDateTime after, LocalDateTime before, String email);
}
