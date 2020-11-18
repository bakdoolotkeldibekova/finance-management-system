package com.example.fms.service;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.*;

import com.example.fms.exception.NotEnoughBalanceException;
import com.example.fms.exception.ResourceNotFoundException;
import com.example.fms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CounterpartyRepository counterpartyRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private JournalRepository journalRepository;

    @Override
    public List<Transaction> getAllForUser() {
        return transactionRepository.findAllByDeleted(false);
    }
//
//    @Override
//    public ResponseEntity<Transaction> addTransaction(Object object, String userEmail) {
//        Transaction transaction = new Transaction();
//        Long categoryId = null;
//        Long counterpartyId = null;
//        Long projectId = null;
//        BigDecimal balance = null;
//        Long toAccountId = null;
//        Long fromAccountId = null;
//        String description = null;
//
//        switch (object.getClass().getSimpleName()) {
//            case "TransactionIncomeDTO":
//                transaction.setAction("INCOME");
//
//                TransactionIncomeDTO transactionIncomeDTO = (TransactionIncomeDTO) object;
//                categoryId = transactionIncomeDTO.getCategory();
//                counterpartyId = transactionIncomeDTO.getCounterparty();
//                projectId = transactionIncomeDTO.getProject();
//                balance = transactionIncomeDTO.getBalance();
//                toAccountId = transactionIncomeDTO.getToAccount();
//                description = transactionIncomeDTO.getDescription();
//                break;
//            case "TransactionExpenseDTO":
//                transaction.setAction("EXPENSE");
//
//                TransactionExpenseDTO transactionExpenseDTO = (TransactionExpenseDTO) object;
//                categoryId = transactionExpenseDTO.getCategory();
//                counterpartyId = transactionExpenseDTO.getCounterparty();
//                projectId = transactionExpenseDTO.getProject();
//                balance = transactionExpenseDTO.getBalance();
//                fromAccountId = transactionExpenseDTO.getFromAccount();
//                description = transactionExpenseDTO.getDescription();
//                break;
//            case "TransactionRemittanceDTO":
//                transaction.setAction("REMITTANCE");
//
//                TransactionRemittanceDTO transactionRemittanceDTO = (TransactionRemittanceDTO) object;
//                fromAccountId = transactionRemittanceDTO.getFromAccount();
//                toAccountId = transactionRemittanceDTO.getToAccount();
//                balance = transactionRemittanceDTO.getBalance();
//                description = transactionRemittanceDTO.getDescription();
//                break;
//        }
//
//        if (transaction.getAction().equals("INCOME") || transaction.getAction().equals("EXPENSE")) {
//            Long finalCategoryId = categoryId;
//            Category category = categoryRepository.findById(categoryId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Category id " + finalCategoryId + " not found!"));
//            transaction.setCategory(category);
//
//            Long finalCounterpartyId = counterpartyId;
//            Counterparty counterparty = counterpartyRepository.findById(counterpartyId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Counterparty id " + finalCounterpartyId + " not found!"));
//            transaction.setCounterparty(counterparty);
//
//            Long finalProjectId = projectId;
//            Project project = projectRepository.findById(projectId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Project id " + finalProjectId + " not found!"));
//            transaction.setProject(project);
//        } if (transaction.getAction().equals("INCOME") || transaction.getAction().equals("REMITTANCE")){
//            Long finalToAccountId = toAccountId;
//            Account account = accountRepository.findById(toAccountId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Account id " + finalToAccountId + " not found!"));
//            transaction.setToAccount(account);
//            account.setBalance(account.getBalance().add(transaction.getBalance()));
//            accountRepository.save(account);
//        } if (transaction.getAction().equals("EXPENSE") || transaction.getAction().equals("REMITTANCE")){
//            Long finalFromAccountId = fromAccountId;
//            Account account = accountRepository.findById(fromAccountId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Account id " + finalFromAccountId + " not found!"));
//            transaction.setFromAccount(account);
//            BigDecimal a = account.getBalance().subtract(transaction.getBalance());
//            if (BigDecimal.ZERO.compareTo(a) == 1)
//                throw new NotEnoughBalanceException("Not enough balance in account id " + fromAccountId + "!");
//            account.setBalance(a);
//            accountRepository.save(account);
//        }
//
//        transaction.setBalance(balance);
//        transaction.setDescription(description);
//        transaction.setUser(userRepository.findByEmail(userEmail));
//        transaction.setDeleted(false);
//
//        return ResponseEntity.ok().body(transaction);
//    }

    @Override
    public List<Transaction> getAllForAdmin() {
        return transactionRepository.findAll();
    }

    @Override
    public ResponseEntity<Transaction> addIncome(TransactionIncomeDTO transactionIncomeDTO, String userEmail) {
        Transaction transaction = new Transaction();
        transaction.setAction("INCOME");

        Category category = categoryRepository.findById(transactionIncomeDTO.getCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Category id " + transactionIncomeDTO.getCategory() + " not found!"));
        transaction.setCategory(category);

        Counterparty counterparty = counterpartyRepository.findById(transactionIncomeDTO.getCounterparty())
                .orElseThrow(() -> new ResourceNotFoundException("Counterparty id " + transactionIncomeDTO.getCounterparty() + " not found!"));
        transaction.setCounterparty(counterparty);

        transaction.setBalance(transactionIncomeDTO.getBalance());

        Project project = projectRepository.findById(transactionIncomeDTO.getProject())
                .orElseThrow(() -> new ResourceNotFoundException("Project id " + transactionIncomeDTO.getProject() + " not found!"));
        transaction.setProject(project);

        Account account = accountRepository.findById(transactionIncomeDTO.getToAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Account id " + transactionIncomeDTO.getToAccount() + " not found!"));
        transaction.setToAccount(account);

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

        Category category = categoryRepository.findById(transactionExpenseDTO.getCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Category id " + transactionExpenseDTO.getCategory() + " not found!"));
        transaction.setCategory(category);

        Counterparty counterparty = counterpartyRepository.findById(transactionExpenseDTO.getCounterparty())
                .orElseThrow(() -> new ResourceNotFoundException("Counterparty id " + transactionExpenseDTO.getCounterparty() + " not found!"));
        transaction.setCounterparty(counterparty);

        transaction.setBalance(transactionExpenseDTO.getBalance());

        Project project = projectRepository.findById(transactionExpenseDTO.getProject())
                .orElseThrow(() -> new ResourceNotFoundException("Project id " + transactionExpenseDTO.getProject() + " not found!"));
        transaction.setProject(project);

        Account account = accountRepository.findById(transactionExpenseDTO.getFromAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Account id " + transactionExpenseDTO.getFromAccount() + " not found!"));
        transaction.setFromAccount(account);

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
        Account fromAccount = accountRepository.findById(transactionRemittanceDTO.getFromAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Account id " + transactionRemittanceDTO.getFromAccount() + " not found!"));
        transaction.setFromAccount(fromAccount);

        Account toAccount = accountRepository.findById(transactionRemittanceDTO.getToAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Account id " + transactionRemittanceDTO.getToAccount() + " not found!"));
        transaction.setToAccount(toAccount);

        BigDecimal a = fromAccount.getBalance().subtract(transaction.getBalance());
        if (BigDecimal.ZERO.compareTo(a) == 1) {
            throw new NotEnoughBalanceException("Not enough balance in account id " + transactionRemittanceDTO.getFromAccount() + "!");
        } else {
            fromAccount.setBalance(a);
        }

        transaction.setBalance(transactionRemittanceDTO.getBalance());
        transaction.setDescription(transactionRemittanceDTO.getDescription());
        transaction.setUser(userRepository.findByEmail(userEmail));
        transaction.setDeleted(false);

        //fromAccount.setBalance(fromAccount.getBalance().subtract(transaction.getBalance()));
        accountRepository.save(fromAccount);

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

        Account oldAccount = accountRepository.findById(oldTransaction.getToAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account id " + oldTransaction.getToAccount() + " not found!"));

        Account newAccount = accountRepository.findById(newTransaction.getToAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Account id " + newTransaction.getToAccount() + " not found!"));

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

        //oldTransaction.setToAccount(newAccount);
        Category category = categoryRepository.findById(newTransaction.getCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Category id " + newTransaction.getCategory() + " not found!"));
        //oldTransaction.setCategory(category);

        Project project = projectRepository.findById(newTransaction.getProject())
                .orElseThrow(() -> new ResourceNotFoundException("Project id " + newTransaction.getProject() + " not found!"));
        //oldTransaction.setProject(project);

        Counterparty counterparty = counterpartyRepository.findById(newTransaction.getCounterparty())
                .orElseThrow(() -> new ResourceNotFoundException("Counterparty id " + newTransaction.getCounterparty() + " not found!"));
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
                    transaction.setUser(userRepository.findByEmail(userEmail));
                    transaction.setDeleted(false);
                    return transactionRepository.save(transaction);
                }).orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<Transaction> updateExpenseById(TransactionExpenseDTO newTransaction, Long id, String userEmail) {
        Transaction oldTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));

        if (oldTransaction.isDeleted())
            throw new ResourceNotFoundException("Transaction id " + id + " was deleted!");

        Account oldAccount = accountRepository.findById(oldTransaction.getFromAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account id " + oldTransaction.getFromAccount() + " not found!"));

        Account newAccount = accountRepository.findById(newTransaction.getFromAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Account id " + newTransaction.getFromAccount() + " not found!"));

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

        Category category = categoryRepository.findById(newTransaction.getCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Category id " + newTransaction.getCategory() + " not found!"));

        Project project = projectRepository.findById(newTransaction.getProject())
                .orElseThrow(() -> new ResourceNotFoundException("Project id " + newTransaction.getProject() + " not found!"));

        Counterparty counterparty = counterpartyRepository.findById(newTransaction.getCounterparty())
                .orElseThrow(() -> new ResourceNotFoundException("Counterparty id " + newTransaction.getCounterparty() + " not found!"));

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

        Account oldFromAccount = accountRepository.findById(oldTransaction.getFromAccount().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Account id " + oldTransaction.getFromAccount() + " not found!"));

        Account newFromAccount = accountRepository.findById(newTransaction.getFromAccount()).orElseThrow(() ->
                new ResourceNotFoundException("Account id " + newTransaction.getFromAccount() + " not found!"));

        Account oldToAccount = accountRepository.findById(oldTransaction.getToAccount().getId()).orElseThrow(() ->
                new ResourceNotFoundException("Account id " + oldTransaction.getToAccount() + " not found!"));

        Account newToAccount = accountRepository.findById(newTransaction.getToAccount()).orElseThrow(() ->
                new ResourceNotFoundException("Account id " + newTransaction.getToAccount() + " not found!"));

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
}
