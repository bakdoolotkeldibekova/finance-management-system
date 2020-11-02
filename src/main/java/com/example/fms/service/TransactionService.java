package com.example.fms.service;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.Transaction;
import com.example.fms.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    List<Transaction> getAll();

    Transaction addIncome(TransactionIncomeDTO transactionIncomeDTO, String userEmail) throws Exception;

    Transaction addExpense(TransactionExpenseDTO transactionExpenseDTO, String userEmail) throws Exception;

    Transaction addRemittance(TransactionRemittanceDTO transactionRemittanceDTO, String userEmail) throws Exception;

    Transaction getTransactionById (Long id) throws Exception;

    List<Transaction> getByAction(String action);

    List<Transaction> getByUserId(Long userId);

    List<Transaction> getByAccountId(Long accountId);

    List<Transaction> getByDate(String after, String before);

    List<Transaction> getByCategory(Long categoryId);

    Transaction updateTransactionById(Transaction newTransaction, String userEmail) throws Exception;

    boolean deleteTransactionById (Long id, String userEmail);
}
