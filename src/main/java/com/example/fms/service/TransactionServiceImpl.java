package com.example.fms.service;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.*;

import com.example.fms.exception.NotEnoughBalanceException;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CounterpartyService counterpartyService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private JournalRepository journalRepository;

    @Override
    public List<Transaction> getAllForUser() {
        return transactionRepository.findAllByDeleted(false);
    }

    @Override
    public List<Transaction> getAllForAdmin() {
        return transactionRepository.findAll();
    }

    @Override
    public ResponseEntity<Transaction> addIncome(TransactionIncomeDTO transactionIncomeDTO, String userEmail) {
        Transaction transaction = new Transaction();
        transaction.setAction("INCOME");

        Category category = categoryService.getCategoryById(transactionIncomeDTO.getCategory()).getBody();
        transaction.setCategory(category);

        Counterparty counterparty = counterpartyService.getCounterpartyById(transactionIncomeDTO.getCounterparty()).getBody();
        transaction.setCounterparty(counterparty);

        Project project = projectService.getProjectById(transactionIncomeDTO.getProject()).getBody();
        transaction.setProject(project);

        Account account = accountService.getAccountById(transactionIncomeDTO.getToAccount()).getBody();
        transaction.setToAccount(account);

        transaction.setBalance(transactionIncomeDTO.getBalance());
        transaction.setDescription(transactionIncomeDTO.getDescription());
        transaction.setUser(userRepository.findByEmail(userEmail));
        transaction.setDeleted(false);

        account.setBalance(account.getBalance().add(transaction.getBalance()));
        accountRepository.save(account);

        transactionRepository.save(transaction);
        return ResponseEntity.ok().body(transaction);
    }

    @Override
    public ResponseEntity<Transaction> addExpense(TransactionExpenseDTO transactionExpenseDTO, String userEmail) {
        Transaction transaction = new Transaction();
        transaction.setAction("EXPENSE");

        Category category = categoryService.getCategoryById(transactionExpenseDTO.getCategory()).getBody();
        transaction.setCategory(category);

        Counterparty counterparty = counterpartyService.getCounterpartyById(transactionExpenseDTO.getCounterparty()).getBody();
        transaction.setCounterparty(counterparty);

        Project project =projectService.getProjectById(transactionExpenseDTO.getProject()).getBody();
        transaction.setProject(project);

        Account account = accountService.getAccountById(transactionExpenseDTO.getFromAccount()).getBody();
        transaction.setFromAccount(account);

        transaction.setBalance(transactionExpenseDTO.getBalance());
        transaction.setDescription(transactionExpenseDTO.getDescription());
        transaction.setUser(userRepository.findByEmail(userEmail));
        transaction.setDeleted(false);

        BigDecimal a = account.getBalance().subtract(transaction.getBalance());
        if (BigDecimal.ZERO.compareTo(a) == 1) {
            throw new NotEnoughBalanceException("Not enough balance in account id " + transactionExpenseDTO.getFromAccount() + "!");
        } else {
            account.setBalance(a);
        }
        accountRepository.save(account);

        transactionRepository.save(transaction);
        return ResponseEntity.ok().body(transaction);
    }

    @Override
    public ResponseEntity<Transaction> addRemittance(TransactionRemittanceDTO transactionRemittanceDTO, String userEmail)  {
        Transaction transaction = new Transaction();
        transaction.setAction("REMITTANCE");
        Account fromAccount = accountService.getAccountById(transactionRemittanceDTO.getFromAccount()).getBody();
        transaction.setFromAccount(fromAccount);

        Account toAccount = accountService.getAccountById(transactionRemittanceDTO.getToAccount()).getBody();
        transaction.setToAccount(toAccount);

        BigDecimal a = fromAccount.getBalance().subtract(transactionRemittanceDTO.getBalance());
        if (BigDecimal.ZERO.compareTo(a) == 1) {
            throw new NotEnoughBalanceException("Not enough balance in account id " + transactionRemittanceDTO.getFromAccount() + "!");
        } else {
            fromAccount.setBalance(a);
            accountRepository.save(fromAccount);
        }

        transaction.setBalance(transactionRemittanceDTO.getBalance());
        transaction.setDescription(transactionRemittanceDTO.getDescription());
        transaction.setUser(userRepository.findByEmail(userEmail));
        transaction.setDeleted(false);

        toAccount.setBalance(toAccount.getBalance().add(transaction.getBalance()));
        accountRepository.save(toAccount);

        transactionRepository.save(transaction);
        return ResponseEntity.ok().body(transaction);
    }

    @Override
    public ResponseEntity<Transaction> getByIdForAdmin(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));
        return ResponseEntity.ok().body(transaction);
    }

    @Override
    public ResponseEntity<Transaction> getByIdForUser(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));
        if (transaction.isDeleted())
            throw new ResourceNotFoundException("Transaction id " + id + " not found!");
        return ResponseEntity.ok().body(transaction);
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
    public ResponseEntity<Transaction> updateIncomeById(TransactionIncomeDTO newTransaction, Long id, String userEmail) {
        Transaction oldTransaction = transactionRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));

        if (oldTransaction.isDeleted())
            throw new ResourceNotFoundException("Transaction id " + id + " was deleted!");

        Account oldAccount = accountService.getAccountById(oldTransaction.getToAccount().getId()).getBody();
        Account newAccount = accountService.getAccountById(newTransaction.getToAccount()).getBody();

        BigDecimal balance;
        if (oldAccount == newAccount) {
            balance = newAccount.getBalance().add(newTransaction.getBalance().subtract(oldTransaction.getBalance()));
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new NotEnoughBalanceException("Not enough balance in account id " + newAccount.getId() + "!");
        } else {
            balance = oldAccount.getBalance().subtract(newTransaction.getBalance());
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new NotEnoughBalanceException("Not enough balance in account id " + oldAccount.getId() + "!");
            oldAccount.setBalance(balance);
            balance = newAccount.getBalance().add(newTransaction.getBalance());
        }
        newAccount.setBalance(balance);

        Category category = categoryService.getCategoryById(newTransaction.getCategory()).getBody();
        Project project = projectService.getProjectById(newTransaction.getProject()).getBody();
        Counterparty counterparty = counterpartyService.getCounterpartyById(newTransaction.getCounterparty()).getBody();

        Transaction result = transactionRepository.findById(id)
                .map(transaction -> {
                    transaction.setCategory(category);
                    transaction.setToAccount(newAccount);
                    transaction.setBalance(newTransaction.getBalance());
                    transaction.setProject(project);
                    transaction.setCounterparty(counterparty);
                    transaction.setDescription(newTransaction.getDescription());
                    transaction.setUser(userRepository.findByEmail(userEmail));
                    transaction.setDeleted(false);
                    return transactionRepository.save(transaction);
                }).orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<Transaction> updateExpenseById(TransactionExpenseDTO newTransaction, Long id, String userEmail) {
        Transaction oldTransaction = transactionRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));

        if (oldTransaction.isDeleted())
            throw new ResourceNotFoundException("Transaction id " + id + " was deleted!");

        Account oldAccount = accountService.getAccountById(oldTransaction.getFromAccount().getId()).getBody();
        Account newAccount = accountService.getAccountById(newTransaction.getFromAccount()).getBody();

        BigDecimal balance;
        if (oldAccount == newAccount) {
            balance = newAccount.getBalance().add(oldTransaction.getBalance().subtract(newTransaction.getBalance()));
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new NotEnoughBalanceException("Not enough balance in account id " + newAccount.getId() + "!");
        } else {
            balance = oldAccount.getBalance().add(newTransaction.getBalance());
            oldAccount.setBalance(balance);
            balance = newAccount.getBalance().subtract(newTransaction.getBalance());
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new NotEnoughBalanceException("Not enough balance in account id " + oldAccount.getId() + "!");
        }
        newAccount.setBalance(balance);

        Category category = categoryService.getCategoryById(newTransaction.getCategory()).getBody();
        Project project = projectService.getProjectById(newTransaction.getProject()).getBody();
        Counterparty counterparty = counterpartyService.getCounterpartyById(newTransaction.getCounterparty()).getBody();

        Transaction result = transactionRepository.findById(id)
                .map(transaction -> {
                    transaction.setCategory(category);
                    transaction.setFromAccount(newAccount);
                    transaction.setBalance(newTransaction.getBalance());
                    transaction.setProject(project);
                    transaction.setCounterparty(counterparty);
                    transaction.setDescription(newTransaction.getDescription());
                    transaction.setUser(userRepository.findByEmail(userEmail));
                    transaction.setDeleted(false);
                    return transactionRepository.save(transaction);
                }).orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<Transaction> updateRemittanceById(TransactionRemittanceDTO newTransaction, Long id, String userEmail) {
        Transaction oldTransaction = transactionRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Transaction id " + id + " not found!"));

        if (oldTransaction.isDeleted())
            throw new ResourceNotFoundException("Transaction id " + id + " was deleted!");

        Account oldFromAccount = accountService.getAccountById(oldTransaction.getFromAccount().getId()).getBody();
        Account newFromAccount = accountService.getAccountById(newTransaction.getFromAccount()).getBody();
        Account oldToAccount = accountService.getAccountById(oldTransaction.getToAccount().getId()).getBody();
        Account newToAccount = accountService.getAccountById(newTransaction.getToAccount()).getBody();

        BigDecimal balance;
        if (oldFromAccount == newFromAccount) {
            balance = oldFromAccount.getBalance().add(oldTransaction.getBalance().subtract(newTransaction.getBalance()));
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new NotEnoughBalanceException("Not enough balance in account id " + oldFromAccount.getId() + "!");
            oldFromAccount.setBalance(balance);

            if (oldToAccount == newToAccount) {
                balance = oldToAccount.getBalance().subtract(oldTransaction.getBalance().subtract(newTransaction.getBalance()));
                if (BigDecimal.ZERO.compareTo(balance) == 1)
                    throw new NotEnoughBalanceException("Not enough balance in account id " + oldToAccount.getId() + "!");
                oldToAccount.setBalance(balance);
            } else {
                balance = oldToAccount.getBalance().subtract(oldTransaction.getBalance());
                if (BigDecimal.ZERO.compareTo(balance) == 1)
                    throw new NotEnoughBalanceException("Not enough balance in account id " + oldToAccount.getId() + "!");
                oldToAccount.setBalance(balance);

                balance = newToAccount.getBalance().add(newTransaction.getBalance());
                newToAccount.setBalance(balance);
            }
        } else {
            balance = oldFromAccount.getBalance().add(oldTransaction.getBalance());
            oldFromAccount.setBalance(balance);

            balance = newFromAccount.getBalance().subtract(newTransaction.getBalance());
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new NotEnoughBalanceException("Not enough balance in account id " + newFromAccount.getId() + "!");
            newFromAccount.setBalance(balance);

            if (oldToAccount == newToAccount) {
                balance = newToAccount.getBalance().subtract(oldTransaction.getBalance().subtract(newTransaction.getBalance()));
                if (BigDecimal.ZERO.compareTo(balance) == 1)
                    throw new NotEnoughBalanceException("Not enough balance in account id " + newToAccount.getId() + "!");
                newToAccount.setBalance(balance);
            } else {
                balance = oldToAccount.getBalance().subtract(oldTransaction.getBalance());
                if (BigDecimal.ZERO.compareTo(balance) == 1)
                    throw new NotEnoughBalanceException("Not enough balance in account id " + oldToAccount.getId() + "!");
                oldToAccount.setBalance(balance);

                balance = newToAccount.getBalance().add(newTransaction.getBalance());
                if (BigDecimal.ZERO.compareTo(balance) == 1)
                    throw new NotEnoughBalanceException("Not enough balance in account id " + newToAccount.getId() + "!");
                newToAccount.setBalance(balance);
            }

        }

        Transaction result = transactionRepository.findById(id)
                .map(transaction -> {
                    transaction.setFromAccount(newFromAccount);
                    transaction.setToAccount(newToAccount);
                    transaction.setBalance(newTransaction.getBalance());
                    transaction.setDescription(newTransaction.getDescription());
                    transaction.setUser(userRepository.findByEmail(userEmail));
                    transaction.setDeleted(false);
                    return transactionRepository.save(transaction);
                }).orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseMessage deleteTransactionById(Long id, String userEmail) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Transaction id " + id + " not found!"));
        if (transaction.isDeleted())
            throw new ResourceNotFoundException("Transaction id " + id + " was deleted!");

        if (transaction.getAction().equals("INCOME") || transaction.getAction().equals("REMITTANCE")){
            Account toAccount = accountRepository.findById(transaction.getToAccount().getId())
                    .orElseThrow(()-> new ResourceNotFoundException("Account id " + transaction.getToAccount().getId() + " not found!"));
            BigDecimal balance = toAccount.getBalance().subtract(transaction.getBalance());
            if (BigDecimal.ZERO.compareTo(balance) == 1)
                throw new NotEnoughBalanceException("Not enough balance in account id " + toAccount.getId() + "!");
            toAccount.setBalance(balance);
            accountRepository.save(toAccount);
        }
        if (transaction.getAction().equals("EXPENSE") || transaction.getAction().equals("REMITTANCE")){
            Account fromAccount = accountRepository.findById(transaction.getFromAccount().getId())
                    .orElseThrow(()-> new ResourceNotFoundException("Account id " + transaction.getFromAccount().getId() + " not found!"));
            BigDecimal balance = fromAccount.getBalance().add(transaction.getBalance());
            fromAccount.setBalance(balance);
            accountRepository.save(fromAccount);
        }
        transaction.setDeleted(true);
        transactionRepository.save(transaction);

        Journal journal = new Journal(); //всее действия сохр в транзакции, только del в журнале
        journal.setTable("TRANSACTION: " + transaction.getAction());
        journal.setAction("delete");
        journal.setUser(userRepository.findByEmail(userEmail));
        journal.setDeleted(false);
        journalRepository.save(journal);

        return new ResponseMessage(HttpStatus.OK.value(), "Transaction successfully deleted");
    }
    @Override
    public Page<Transaction> getByPage(List<Transaction> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<Transaction>(list.subList(start, end), pageable, list.size());
    }
}
