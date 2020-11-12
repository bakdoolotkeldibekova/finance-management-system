package com.example.fms.service;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.*;

import com.example.fms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private CounterpartyRepository counterpartyRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<Transaction> getAllForUser() {
        return transactionRepository.findAllByDeleted(false);
    }

    @Override
    public List<Transaction> getAllForAdmin() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction addIncome(TransactionIncomeDTO transactionIncomeDTO, String userEmail) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAction("INCOME");
        transaction.setCategory(categoryRepository.findById(transactionIncomeDTO.getCategory()).orElseThrow(Exception::new));
        transaction.setCounterparty(counterpartyRepository.findById(transactionIncomeDTO.getCounterparty()).orElseThrow(Exception::new));
        transaction.setBalance(transactionIncomeDTO.getBalance());
        transaction.setProject(projectRepository.findById(transactionIncomeDTO.getProject()).orElseThrow(Exception::new));
        transaction.setToAccount(accountRepository.findById(transactionIncomeDTO.getToAccount()).orElseThrow(Exception::new));
        transaction.setDescription(transactionIncomeDTO.getDescription());
        transaction.setUser(userService.getByEmail(userEmail));
        transaction.setDeleted(false);

        Account account = accountRepository.findById(transaction.getToAccount().getId()).orElseThrow(Exception::new);
        account.setBalance(account.getBalance().add(transaction.getBalance()));
        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction addExpense(TransactionExpenseDTO transactionExpenseDTO, String userEmail) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAction("EXPENSE");
        transaction.setCategory(categoryRepository.findById(transactionExpenseDTO.getCategory()).orElseThrow(Exception::new));
        transaction.setCounterparty(counterpartyRepository.findById(transactionExpenseDTO.getCounterparty()).orElseThrow(Exception::new));
        transaction.setBalance(transactionExpenseDTO.getBalance());
        transaction.setProject(projectRepository.findById(transactionExpenseDTO.getProject()).orElseThrow(Exception::new));
        transaction.setFromAccount(accountRepository.findById(transactionExpenseDTO.getFromAccount()).orElseThrow(Exception::new));
        transaction.setDescription(transactionExpenseDTO.getDescription());
        transaction.setUser(userService.getByEmail(userEmail));
        transaction.setDeleted(false);

        Account account = accountRepository.findById(transaction.getFromAccount().getId()).orElseThrow(Exception::new);
        BigDecimal a = account.getBalance().subtract(transaction.getBalance());
        if (BigDecimal.ZERO.compareTo(a) == 1) {
            throw new Exception("Not enough balance in account!");
        } else {
            account.setBalance(a);
        }

        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }
    @Override
    public Transaction addRemittance(TransactionRemittanceDTO transactionRemittanceDTO, String userEmail) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAction("REMITTANCE");
        transaction.setFromAccount(accountRepository.findById(transactionRemittanceDTO.getFromAccount()).orElseThrow(Exception::new));
        transaction.setToAccount(accountRepository.findById(transactionRemittanceDTO.getToAccount()).orElseThrow(Exception::new));
        transaction.setBalance(transactionRemittanceDTO.getBalance());
        transaction.setDescription(transactionRemittanceDTO.getDescription());
        transaction.setUser(userService.getByEmail(userEmail));
        transaction.setDeleted(false);

        Account accountFrom = accountRepository.findById(transaction.getFromAccount().getId()).orElseThrow(Exception::new);
        accountFrom.setBalance(accountFrom.getBalance().subtract(transaction.getBalance()));
        accountRepository.save(accountFrom);

        Account accountTo = accountRepository.findById(transaction.getToAccount().getId()).orElseThrow(Exception::new);
        accountTo.setBalance(accountTo.getBalance().add(transaction.getBalance()));
        accountRepository.save(accountTo);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransactionById(Long id) throws Exception {
        return transactionRepository.findById(id).orElseThrow(Exception::new);
    }

    @Override
    public List<Transaction> getAllByAction(String action) {
        return transactionRepository.findAllByActionContainingIgnoringCase(action);
    }

    @Override
    public List<Transaction> getAllByUserId(Long userId) {
        return transactionRepository.findAllByUserId(userId);
    }

    @Override
    public List<Transaction> getAllByFromAccount(Long accountId) {
        return transactionRepository.findAllByFromAccountId(accountId);
    }

    @Override
    public List<Transaction> getAllByToAccount(Long accountId) {
        return transactionRepository.findAllByToAccountId(accountId);
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public List<Transaction> getAllByDateCreatedAfter(String after) {
        return transactionRepository.findAllByDateCreatedAfter(LocalDateTime.parse(after, formatter));
    }

    @Override
    public List<Transaction> getAllByDateCreatedBefore(String before) {
        return transactionRepository.findAllByDateCreatedBefore(LocalDateTime.parse(before, formatter));
    }

    @Override
    public List<Transaction> getAllByProject(Long projectId) {
        return transactionRepository.findAllByProjectId(projectId);
    }

    @Override
    public List<Transaction> getAllByCounterparty(Long counterpartyId) {
        return transactionRepository.findAllByCounterpartyId(counterpartyId);
    }

    @Override
    public List<Transaction> getAllByCategory(Long categoryId) {
        return transactionRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public List<Transaction> getAllByBalanceGreaterThanEqual(BigDecimal balance) {
        return transactionRepository.findAllByBalanceGreaterThanEqual(balance);
    }

    @Override
    public List<Transaction> getAllByBalanceLessThanEqual(BigDecimal balance) {
        return transactionRepository.findAllByBalanceLessThanEqual(balance);
    }

    @Override
    public Transaction updateIncomeById(TransactionIncomeDTO newTransaction, String userEmail) throws Exception {
        Account toAccount = accountRepository.findById(newTransaction.getToAccount()).orElseThrow(Exception::new);
        Category category = categoryRepository.findById(newTransaction.getCategory()).orElseThrow(Exception::new);
        Project project = projectRepository.findById(newTransaction.getProject()).orElseThrow(Exception::new);
        Counterparty counterparty = counterpartyRepository.findById(newTransaction.getCounterparty()).orElseThrow(Exception::new);
        Transaction result = transactionRepository.findById(newTransaction.getId())
                .map(transaction -> {
                    transaction.setCategory(category);
                    transaction.setToAccount(toAccount);
                    transaction.setBalance(newTransaction.getBalance());
                    transaction.setProject(project);
                    transaction.setCounterparty(counterparty);
                    transaction.setDescription(newTransaction.getDescription());
                    transaction.setUser(userService.getByEmail(userEmail));
                    transaction.setDeleted(false);
                    return transactionRepository.save(transaction);
                }).orElseThrow(Exception::new);
        return result;
    }

    @Override
    public Transaction updateExpenseById(TransactionExpenseDTO newTransaction, String userEmail) throws Exception {
        Account fromAccount = accountRepository.findById(newTransaction.getFromAccount()).orElseThrow(Exception::new);
        Category category = categoryRepository.findById(newTransaction.getCategory()).orElseThrow(Exception::new);
        Project project = projectRepository.findById(newTransaction.getProject()).orElseThrow(Exception::new);
        Counterparty counterparty = counterpartyRepository.findById(newTransaction.getCounterparty()).orElseThrow(Exception::new);
        Transaction result = transactionRepository.findById(newTransaction.getId())
                .map(transaction -> {
                    transaction.setCategory(category);
                    transaction.setToAccount(fromAccount);
                    transaction.setBalance(newTransaction.getBalance());
                    transaction.setProject(project);
                    transaction.setCounterparty(counterparty);
                    transaction.setDescription(newTransaction.getDescription());
                    transaction.setUser(userService.getByEmail(userEmail));
                    transaction.setDeleted(false);
                    return transactionRepository.save(transaction);
                }).orElseThrow(Exception::new);
        return result;
    }

    @Override
    public Transaction updateRemittanceById(TransactionRemittanceDTO newTransaction, String userEmail) throws Exception {
        Account fromAccount = accountRepository.findById(newTransaction.getFromAccount()).orElseThrow(Exception::new);
        Account toAccount = accountRepository.findById(newTransaction.getToAccount()).orElseThrow(Exception::new);
        Transaction result = transactionRepository.findById(newTransaction.getId())
                .map(transaction -> {
                    transaction.setFromAccount(fromAccount);
                    transaction.setToAccount(toAccount);
                    transaction.setBalance(newTransaction.getBalance());
                    transaction.setDescription(newTransaction.getDescription());
                    transaction.setUser(userService.getByEmail(userEmail));
                    transaction.setDeleted(false);
                    return transactionRepository.save(transaction);
                }).orElseThrow(Exception::new);
        return result;
    }

    @Override
    public boolean deleteTransactionById(Long id, String userEmail) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        if (transaction != null){
            transaction.setDeleted(true);

            Journal journal = new Journal(); //всее действия сохр в транзакции, только del в журнале
            journal.setTable("TRANSACTION: " + transaction.getAction());
            journal.setAction("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journal.setDeleted(false);
            journalRepository.save(journal);
            return true;
        }
        return false;
    }
}
