package com.example.fms.service;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.*;

import com.example.fms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public Transaction addIncome(TransactionIncomeDTO transactionIncomeDTO, String userEmail) {
        Transaction transaction = new Transaction();
        transaction.setAction("INCOME");

        Category category = categoryRepository.findById(transactionIncomeDTO.getCategory()).orElse(null);
        if (category == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category id " + transactionIncomeDTO.getCategory() + " not found!");
        transaction.setCategory(category);

        Counterparty counterparty = counterpartyRepository.findById(transactionIncomeDTO.getCounterparty()).orElse(null);
        if (counterparty == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Counterparty id " + transactionIncomeDTO.getCounterparty() + " not found!");
        transaction.setCounterparty(counterparty);

        transaction.setBalance(transactionIncomeDTO.getBalance());

        Project project = projectRepository.findById(transactionIncomeDTO.getProject()).orElse(null);
        if (project == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project id " + transactionIncomeDTO.getProject() + " not found!");
        transaction.setProject(project);

        Account account = accountRepository.findById(transactionIncomeDTO.getToAccount()).orElse(null);
        if (account == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + transactionIncomeDTO.getToAccount() + " not found!");
        transaction.setToAccount(account);

        transaction.setDescription(transactionIncomeDTO.getDescription());
        transaction.setUser(userService.getByEmail(userEmail));
        transaction.setDeleted(false);

        account.setBalance(account.getBalance().add(transaction.getBalance()));
        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction addExpense(TransactionExpenseDTO transactionExpenseDTO, String userEmail) {
        Transaction transaction = new Transaction();
        transaction.setAction("EXPENSE");

        Category category = categoryRepository.findById(transactionExpenseDTO.getCategory()).orElse(null);
        if (category == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category id " + transactionExpenseDTO.getCategory() + " not found!");
        transaction.setCategory(category);

        Counterparty counterparty = counterpartyRepository.findById(transactionExpenseDTO.getCounterparty()).orElse(null);
        if (counterparty == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Counterparty id " + transactionExpenseDTO.getCounterparty() + " not found!");
        transaction.setCounterparty(counterparty);

        transaction.setBalance(transactionExpenseDTO.getBalance());

        Project project = projectRepository.findById(transactionExpenseDTO.getProject()).orElse(null);
        if (project == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project id " + transactionExpenseDTO.getProject() + " not found!");
        transaction.setProject(project);

        Account account = accountRepository.findById(transactionExpenseDTO.getFromAccount()).orElse(null);
        if (account == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + transactionExpenseDTO.getFromAccount() + " not found!");
        transaction.setFromAccount(account);

        transaction.setDescription(transactionExpenseDTO.getDescription());
        transaction.setUser(userService.getByEmail(userEmail));
        transaction.setDeleted(false);

        BigDecimal a = account.getBalance().subtract(transaction.getBalance());
        if (BigDecimal.ZERO.compareTo(a) == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + transactionExpenseDTO.getFromAccount() + "!");
        } else {
            account.setBalance(a);
        }

        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }
    @Override
    public Transaction addRemittance(TransactionRemittanceDTO transactionRemittanceDTO, String userEmail)  {
        Transaction transaction = new Transaction();
        transaction.setAction("REMITTANCE");
        Account fromAccount = accountRepository.findById(transactionRemittanceDTO.getFromAccount()).orElse(null);
        if (fromAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + transactionRemittanceDTO.getFromAccount() + " not found!");
        transaction.setFromAccount(fromAccount);

        Account toAccount = accountRepository.findById(transactionRemittanceDTO.getToAccount()).orElse(null);
        if (toAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + transactionRemittanceDTO.getToAccount() + " not found!");
        transaction.setToAccount(toAccount);

        transaction.setBalance(transactionRemittanceDTO.getBalance());
        transaction.setDescription(transactionRemittanceDTO.getDescription());
        transaction.setUser(userService.getByEmail(userEmail));
        transaction.setDeleted(false);

        fromAccount.setBalance(fromAccount.getBalance().subtract(transaction.getBalance()));
        accountRepository.save(fromAccount);

        toAccount.setBalance(toAccount.getBalance().add(transaction.getBalance()));
        accountRepository.save(toAccount);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
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
    public Transaction updateIncomeById(TransactionIncomeDTO newTransaction, Long id, String userEmail) {
        Transaction oldTransaction = transactionRepository.findById(id).orElse(null);
        if (oldTransaction == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");

        Account oldAccount = accountRepository.findById(oldTransaction.getToAccount().getId()).orElse(null);
        if (oldAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + oldAccount.getId() + " not found!");
        Account newAccount = accountRepository.findById(newTransaction.getToAccount()).orElse(null);
        if (newAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + newAccount.getId() + " not found!");

        BigDecimal balance;
        if (oldAccount == newAccount) {
            balance = newAccount.getBalance().add(newTransaction.getBalance().subtract(oldTransaction.getBalance()));
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + newAccount.getId() + "!");
        } else {
            balance = oldAccount.getBalance().subtract(newTransaction.getBalance());
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + oldAccount.getId() + "!");
            oldAccount.setBalance(balance);

            balance = newAccount.getBalance().add(newTransaction.getBalance());
        }
        newAccount.setBalance(balance);

        //oldTransaction.setToAccount(newAccount);
        Category category = categoryRepository.findById(newTransaction.getCategory()).orElse(null);
        if (category == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category id " + newTransaction.getCategory() + " not found!");
        //oldTransaction.setCategory(category);

        Project project = projectRepository.findById(newTransaction.getProject()).orElse(null);
        if (project == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project id " + newTransaction.getProject() + " not found!");
        //oldTransaction.setProject(project);

        Counterparty counterparty = counterpartyRepository.findById(newTransaction.getCounterparty()).orElse(null);
        if (counterparty == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Counterparty id " + newTransaction.getCounterparty() + " not found!");
        //oldTransaction.setCounterparty(counterparty);
        //oldTransaction.setDescription(newTransaction.getDescription());


        Transaction result = transactionRepository.findById(id)
                .map(transaction -> {
                    transaction.setCategory(category);
                    transaction.setToAccount(newAccount);
                    transaction.setBalance(newTransaction.getBalance());
                    transaction.setProject(project);
                    transaction.setCounterparty(counterparty);
                    transaction.setDescription(newTransaction.getDescription());
                    transaction.setUser(userService.getByEmail(userEmail));
                    transaction.setDeleted(false);
                    return transactionRepository.save(transaction);
                }).orElse(null);
        if (result == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
        return result;
    }

    @Override
    public Transaction updateExpenseById(TransactionExpenseDTO newTransaction, Long id, String userEmail) {
        Transaction oldTransaction = transactionRepository.findById(id).orElse(null);
        if (oldTransaction == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");

        Account oldAccount = accountRepository.findById(oldTransaction.getFromAccount().getId()).orElse(null);
        if (oldAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + oldAccount.getId() + " not found!");
        Account newAccount = accountRepository.findById(newTransaction.getFromAccount()).orElse(null);
        if (oldAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + newAccount.getId() + " not found!");

        BigDecimal balance;
        if (oldAccount == newAccount) {
            balance = newAccount.getBalance().add(oldTransaction.getBalance().subtract(newTransaction.getBalance()));
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + newAccount.getId() + "!");
        } else {
            balance = oldAccount.getBalance().add(newTransaction.getBalance());
            oldAccount.setBalance(balance);
            balance = newAccount.getBalance().subtract(newTransaction.getBalance());
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + newAccount.getId() + "!");
        }
        newAccount.setBalance(balance);

        Category category = categoryRepository.findById(newTransaction.getCategory()).orElse(null);
        if (category == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category id " + newTransaction.getCounterparty() + " not found!");

        Project project = projectRepository.findById(newTransaction.getProject()).orElse(null);
        if (project == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project id " + newTransaction.getProject() + " not found!");

        Counterparty counterparty = counterpartyRepository.findById(newTransaction.getCounterparty()).orElse(null);
        if (counterparty == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Counterparty id " + newTransaction.getCounterparty() + " not found!");

        Transaction result = transactionRepository.findById(id)
                .map(transaction -> {
                    transaction.setCategory(category);
                    transaction.setFromAccount(newAccount);
                    transaction.setBalance(newTransaction.getBalance());
                    transaction.setProject(project);
                    transaction.setCounterparty(counterparty);
                    transaction.setDescription(newTransaction.getDescription());
                    transaction.setUser(userService.getByEmail(userEmail));
                    transaction.setDeleted(false);
                    return transactionRepository.save(transaction);
                }).orElse(null);
        if (result == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
        return result;
    }

    @Override
    public Transaction updateRemittanceById(TransactionRemittanceDTO newTransaction, Long id, String userEmail) {
        Transaction oldTransaction = transactionRepository.findById(id).orElse(null);
        if (oldTransaction == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");

        Account oldFromAccount = accountRepository.findById(oldTransaction.getFromAccount().getId()).orElse(null);
        if (oldFromAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + oldFromAccount.getId() + " not found!");
        Account newFromAccount = accountRepository.findById(newTransaction.getFromAccount()).orElse(null);
        if (newFromAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + newFromAccount.getId() + " not found!");
        Account oldToAccount = accountRepository.findById(oldTransaction.getToAccount().getId()).orElse(null);
        if (oldToAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + oldToAccount.getId() + " not found!");
        Account newToAccount = accountRepository.findById(newTransaction.getToAccount()).orElse(null);
        if (newToAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + newToAccount.getId() + " not found!");

        BigDecimal balance;
        if (oldFromAccount == newFromAccount) {
            balance = oldFromAccount.getBalance().add(oldTransaction.getBalance().subtract(newTransaction.getBalance()));
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + oldFromAccount.getId() + "!");
            oldFromAccount.setBalance(balance);

            if (oldToAccount == newToAccount) {
                balance = oldToAccount.getBalance().subtract(oldTransaction.getBalance().subtract(newTransaction.getBalance()));
                if (BigDecimal.ZERO.compareTo(balance) == 1)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + oldToAccount.getId() + "!");
                oldToAccount.setBalance(balance);
            } else {
                balance = oldToAccount.getBalance().subtract(oldTransaction.getBalance());
                if (BigDecimal.ZERO.compareTo(balance) == 1)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + oldToAccount.getId() + "!");
                oldToAccount.setBalance(balance);

                balance = newToAccount.getBalance().add(newTransaction.getBalance());
                newToAccount.setBalance(balance);
            }
        } else {
            balance = oldFromAccount.getBalance().add(oldTransaction.getBalance());
            oldFromAccount.setBalance(balance);

            balance = newFromAccount.getBalance().subtract(newTransaction.getBalance());
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + newFromAccount.getId() + "!");
            newFromAccount.setBalance(balance);

            if (oldToAccount == newToAccount) {
                balance = newToAccount.getBalance().subtract(oldTransaction.getBalance().subtract(newTransaction.getBalance()));
                if (BigDecimal.ZERO.compareTo(balance) == 1)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + newToAccount.getId() + "!");
                newToAccount.setBalance(balance);
            } else {
                balance = oldToAccount.getBalance().subtract(oldTransaction.getBalance());
                if (BigDecimal.ZERO.compareTo(balance) == 1)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + oldToAccount.getId() + "!");
                oldToAccount.setBalance(balance);

                balance = newToAccount.getBalance().add(newTransaction.getBalance());
                if (BigDecimal.ZERO.compareTo(balance) == 1)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + newToAccount.getId() + "!");
                newToAccount.setBalance(balance);
            }

        }

        Transaction result = transactionRepository.findById(id)
                .map(transaction -> {
                    transaction.setFromAccount(newFromAccount);
                    transaction.setToAccount(newToAccount);
                    transaction.setBalance(newTransaction.getBalance());
                    transaction.setDescription(newTransaction.getDescription());
                    transaction.setUser(userService.getByEmail(userEmail));
                    transaction.setDeleted(false);
                    return transactionRepository.save(transaction);
                }).orElse(null);
        if (result == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");
        return result;
    }

    @Override
    public Transaction deleteTransactionById(Long id, String userEmail) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        if (transaction == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction id " + id + " not found!");

        Account fromAccount = accountRepository.findById(transaction.getFromAccount().getId()).orElse(null);
        if (fromAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + fromAccount.getId() + " not found!");

        Account toAccount = accountRepository.findById(transaction.getToAccount().getId()).orElse(null);
        if (toAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id " + toAccount.getId() + " not found!");

        BigDecimal balance = fromAccount.getBalance().add(transaction.getBalance());
        fromAccount.setBalance(balance);

        balance = toAccount.getBalance().subtract(transaction.getBalance());
        if (BigDecimal.ZERO.compareTo(balance) == 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance in account id " + toAccount.getId() + "!");
        toAccount.setBalance(balance);

        transaction.setDeleted(true);

        Journal journal = new Journal(); //всее действия сохр в транзакции, только del в журнале
        journal.setTable("TRANSACTION: " + transaction.getAction());
        journal.setAction("delete");
        journal.setUser(userService.getByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return transaction;
    }
}
