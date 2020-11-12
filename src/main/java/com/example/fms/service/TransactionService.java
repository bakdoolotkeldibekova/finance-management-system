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

    Transaction addIncome(TransactionIncomeDTO transactionIncomeDTO, String userEmail) throws Exception;

    Transaction addExpense(TransactionExpenseDTO transactionExpenseDTO, String userEmail) throws Exception;

    Transaction addRemittance(TransactionRemittanceDTO transactionRemittanceDTO, String userEmail) throws Exception;

    Transaction getTransactionById (Long id) throws Exception;

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

    Transaction updateIncomeById(TransactionIncomeDTO newTransaction, String userEmail) throws Exception;
    Transaction updateExpenseById(TransactionExpenseDTO newTransaction, String userEmail) throws Exception;
    Transaction updateRemittanceById(TransactionRemittanceDTO newTransaction, String userEmail) throws Exception;

    boolean deleteTransactionById (Long id, String userEmail);
}
