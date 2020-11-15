package com.example.fms.service;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    List<Transaction> getAllForAdmin();
    List<Transaction> getAllForUser();

    Transaction addIncome(TransactionIncomeDTO transactionIncomeDTO, String userEmail);

    Transaction addExpense(TransactionExpenseDTO transactionExpenseDTO, String userEmail);

    Transaction addRemittance(TransactionRemittanceDTO transactionRemittanceDTO, String userEmail);

    Transaction getTransactionById (Long id);

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

    Transaction updateIncomeById(TransactionIncomeDTO newTransaction, Long id, String userEmail);
    Transaction updateExpenseById(TransactionExpenseDTO newTransaction, Long id, String userEmail);
    Transaction updateRemittanceById(TransactionRemittanceDTO newTransaction, Long id, String userEmail);

    Transaction deleteTransactionById (Long id, String userEmail);
}
