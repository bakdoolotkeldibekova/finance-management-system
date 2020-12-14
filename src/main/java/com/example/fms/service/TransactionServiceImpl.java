package com.example.fms.service;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.*;

import com.example.fms.exception.AccessDenied;
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
import java.util.*;

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
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private TransactionService transactionService;

    @Override
    public List<Transaction> getAll(boolean isDeleted, String email) {
        User user = userRepository.findByEmail(email);
        if (isDeleted && !user.getRole().getName().equals("ROLE_ADMIN"))
            throw new AccessDenied("Deleted transactions are not available for you");
        List<Department> departments = user.getDepartments();
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction transaction : transactionRepository.findAllByDeletedOrderByDateCreatedDesc(isDeleted)) {
            if (transaction.getAction().equals("REMITTANCE"))
                transactions.add(transaction);
            else if (departments.contains(transaction.getDepartment()))
                transactions.add(transaction);
        }
        return transactions;
    }

//    @Override
//    @Override
//    public ResponseEntity<BigDecimal> income(LocalDateTime after, LocalDateTime before, String email) {
//        BigDecimal income = BigDecimal.ZERO;
//        List<Transaction> transactions = transactionRepository.findAllByDeletedAndActionContainingIgnoringCase(false, "INCOME");
//        for (Transaction transaction : transactions) {
//            income = income.add(transaction.getBalance());
//        }
//        return ResponseEntity.ok().body(income);
//    }

    @Override
    public ResponseEntity<Map<String,BigDecimal>> incomeCategory(LocalDateTime after, LocalDateTime before) {
        Map<String, BigDecimal> map = new HashMap<>();
        List<String> categories = new ArrayList<>();
        List<BigDecimal> transactions = new ArrayList<>();

        for (Category category : categoryService.getAll()) {
            categories.add(category.getName());
            transactions.add(BigDecimal.ZERO);
        }

        for (Transaction transaction : transactionRepository
                .findAllByDeletedAndActionContainingAndDateCreatedAfterAndDateCreatedBefore(false, "INCOME", after, before)) {
            int index = categories.indexOf(transaction.getCategory().getName());
            BigDecimal temp = transaction.getBalance().add(transactions.get(index));
            transactions.set(index, temp);
        }

        for (int i = 0; i < categories.size(); i++) {
            map.put(categories.get(i), transactions.get(i));
        }
        return ResponseEntity.ok().body(map);
    }

    @Override
    public ResponseEntity<Map<String,BigDecimal>> incomeProject(LocalDateTime after, LocalDateTime before) {
        Map<String, BigDecimal> map = new HashMap<>();
        List<String> projects = new ArrayList<>();
        List<BigDecimal> transactions = new ArrayList<>();

        for (Project project : projectService.getAll()) {
            projects.add(project.getName());
            transactions.add(BigDecimal.ZERO);
        }

        for (Transaction transaction : transactionRepository
                .findAllByDeletedAndActionContainingAndDateCreatedAfterAndDateCreatedBefore(false, "INCOME", after, before)) {
            int index = projects.indexOf(transaction.getProject().getName());
            BigDecimal temp = transaction.getBalance().add(transactions.get(index));
            transactions.set(index, temp);
        }

        for (int i = 0; i < transactions.size(); i++)
            map.put(projects.get(i), transactions.get(i));
        return ResponseEntity.ok().body(map);
    }

    @Override
    public ResponseEntity<Map<String,BigDecimal>> incomeCounterparty(LocalDateTime after, LocalDateTime before) {
        Map<String, BigDecimal> map = new HashMap<>();
        List<String> counterparties = new ArrayList<>();
        List<BigDecimal> transactions = new ArrayList<>();

        for (Counterparty counterparty : counterpartyService.getAll()) {
            counterparties.add(counterparty.getName());
            transactions.add(BigDecimal.ZERO);
        }

        for (Transaction transaction : transactionRepository
                .findAllByDeletedAndActionContainingAndDateCreatedAfterAndDateCreatedBefore(false, "INCOME", after, before)) {
            int index = counterparties.indexOf(transaction.getCounterparty().getName());
            BigDecimal temp = transaction.getBalance().add(transactions.get(index));
            transactions.set(index, temp);
        }

        for (int i = 0; i < transactions.size(); i++)
            map.put(counterparties.get(i), transactions.get(i));
        return ResponseEntity.ok().body(map);
    }

    @Override
    public ResponseEntity<Map<String, BigDecimal>> expenseCategory(LocalDateTime after, LocalDateTime before) {
        Map<String, BigDecimal> map = new HashMap<>();
        List<String> categories = new ArrayList<>();
        List<BigDecimal> transactions = new ArrayList<>();

        for (Category category : categoryService.getAll()) {
            categories.add(category.getName());
            transactions.add(BigDecimal.ZERO);
        }

        for (Transaction transaction : transactionRepository
                .findAllByDeletedAndActionContainingAndDateCreatedAfterAndDateCreatedBefore(false, "EXPENSE", after, before)) {
            int index = categories.indexOf(transaction.getCategory().getName());
            BigDecimal temp = transaction.getBalance().add(transactions.get(index));
            transactions.set(index, temp);
        }

        for (int i = 0; i < categories.size(); i++) {
            map.put(categories.get(i), transactions.get(i));
        }
        return ResponseEntity.ok().body(map);
    }

    @Override
    public ResponseEntity<Map<String, BigDecimal>> expenseProject(LocalDateTime after, LocalDateTime before) {
        Map<String, BigDecimal> map = new HashMap<>();
        List<String> projects = new ArrayList<>();
        List<BigDecimal> transactions = new ArrayList<>();

        for (Project project : projectService.getAll()) {
            projects.add(project.getName());
            transactions.add(BigDecimal.ZERO);
        }

        for (Transaction transaction : transactionRepository
                .findAllByDeletedAndActionContainingAndDateCreatedAfterAndDateCreatedBefore(false, "EXPENSE", after, before)) {
            int index = projects.indexOf(transaction.getProject().getName());
            BigDecimal temp = transaction.getBalance().add(transactions.get(index));
            transactions.set(index, temp);
        }

        for (int i = 0; i < transactions.size(); i++)
            map.put(projects.get(i), transactions.get(i));
        return ResponseEntity.ok().body(map);
    }

    @Override
    public ResponseEntity<Map<String, BigDecimal>> expenseCounterparty(LocalDateTime after, LocalDateTime before) {
        Map<String, BigDecimal> map = new HashMap<>();
        List<String> counterparties = new ArrayList<>();
        List<BigDecimal> transactions = new ArrayList<>();

        for (Counterparty counterparty : counterpartyService.getAll()) {
            counterparties.add(counterparty.getName());
            transactions.add(BigDecimal.ZERO);
        }

        for (Transaction transaction : transactionRepository
                .findAllByDeletedAndActionContainingAndDateCreatedAfterAndDateCreatedBefore(false, "EXPENSE", after, before)) {
            int index = counterparties.indexOf(transaction.getCounterparty().getName());
            BigDecimal temp = transaction.getBalance().add(transactions.get(index));
            transactions.set(index, temp);
        }

        for (int i = 0; i < transactions.size(); i++)
            map.put(counterparties.get(i), transactions.get(i));
        return ResponseEntity.ok().body(map);
    }

//    @Override
//    public ResponseEntity<BigDecimal> expense(LocalDateTime after, LocalDateTime before, String email) {
//        BigDecimal expense = BigDecimal.ZERO;
//        List<Transaction> transactions = transactionRepository.findAllByDeletedAndActionContainingIgnoringCase(false, "EXPENSE");
//        for (Transaction transaction : transactions) {
//            expense = expense.add(transaction.getBalance());
//        }
//        return ResponseEntity.ok().body(expense);
//    }

    @Override
    public ResponseEntity<BigDecimal> profit(LocalDateTime after, LocalDateTime before, String email) {
        BigDecimal profit = BigDecimal.ZERO;
//                Objects.requireNonNull(this.income(after, before, email).getBody()).subtract(this.expense(after, before, email).getBody());
////        BigDecimal profit = income.subtract(expense);
        return ResponseEntity.ok().body(profit);
    }

    @Override
    public ResponseEntity<Transaction> addIncome(TransactionIncomeDTO transactionIncomeDTO, String userEmail) {
        Transaction transaction = new Transaction();
        transaction.setAction("INCOME");

        Department department = departmentService.getDepartmentById(transactionIncomeDTO.getDepartment()).getBody();
        if (!userRepository.findByEmail(userEmail).getDepartments().contains(department))
            throw new AccessDenied("You do not have access to the next departmentId: " + department.getId());
        transaction.setDepartment(department);

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

        Department department = departmentService.getDepartmentById(transactionExpenseDTO.getDepartment()).getBody();
        if (!userRepository.findByEmail(userEmail).getDepartments().contains(department))
            throw new AccessDenied("You do not have access to the next departmentId: " + department.getId());
        transaction.setDepartment(department);

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
    public ResponseEntity<Transaction> getByIdForUser(Long id, String userEmail) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));
        if (transaction.isDeleted())
            throw new ResourceNotFoundException("Transaction id " + id + " was deleted!");

        if (transaction.getAction().equals("REMITTANCE"))
            return ResponseEntity.ok().body(transaction);
        if (userRepository.findByEmail(userEmail).getDepartments().contains(transaction.getDepartment()))
            return ResponseEntity.ok().body(transaction);
        throw new AccessDenied("You do not have access to the next departmentId: " + transaction.getDepartment().getId());
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
    public ResponseEntity<Transaction> updateIncomeById(TransactionIncomeDTO transactionIncomeDTO, Long id, String userEmail) {
        Transaction transaction = transactionRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));
        if (transaction.isDeleted())
            throw new ResourceNotFoundException("Transaction id " + id + " was deleted!");
        if (!transaction.getAction().equals("INCOME"))
            throw new ResourceNotFoundException("Transaction id " + id + " is NOT INCOME action!");

        List<Department> departmentList = userRepository.findByEmail(userEmail).getDepartments();
        if (!transaction.getUser().getRole().getName().equals("ROLE_ADMIN") && !departmentList.contains(transaction.getDepartment()))
            throw new AccessDenied("You do not have access to the next departmentId: " + transaction.getDepartment().getId());
        if (!transaction.getUser().getRole().getName().equals("ROLE_ADMIN") && !departmentList.contains(transactionIncomeDTO.getDepartment()))
            throw new AccessDenied("You do not have access to the next departmentId: " + transactionIncomeDTO.getDepartment());

        if (transaction.getDepartment().getId() != transactionIncomeDTO.getDepartment()){
            Department department = departmentService.getDepartmentById(transactionIncomeDTO.getDepartment()).getBody();
            transaction.setDepartment(department);
        }

        if (transaction.getCategory().getId() != transactionIncomeDTO.getCategory()){
            Category category = categoryService.getCategoryById(transactionIncomeDTO.getCategory()).getBody();
            transaction.setCategory(category);
        }
        if (transaction.getProject().getId() != transactionIncomeDTO.getProject()){
            Project project = projectService.getProjectById(transactionIncomeDTO.getProject()).getBody();
            transaction.setProject(project);
        }
        if (transaction.getCounterparty().getId() != transactionIncomeDTO.getCounterparty()){
            Counterparty counterparty = counterpartyService.getCounterpartyById(transactionIncomeDTO.getCounterparty()).getBody();
            transaction.setCounterparty(counterparty);
        }

        Account oldAccount = accountService.getAccountById(transaction.getToAccount().getId()).getBody();
        Account newAccount = accountService.getAccountById(transactionIncomeDTO.getToAccount()).getBody();

        BigDecimal balance;
        if (oldAccount.getId() == newAccount.getId()){
            balance = oldAccount.getBalance().subtract(transaction.getBalance()).add(transactionIncomeDTO.getBalance());
            if (balance.compareTo(BigDecimal.ZERO) < 0)
                throw new NotEnoughBalanceException("Not enough balance in account id " + oldAccount.getId() + "!");
            oldAccount.setBalance(balance);
            transaction.setToAccount(accountRepository.save(oldAccount));
        } else {
            balance = oldAccount.getBalance().subtract(transaction.getBalance());
            if (balance.compareTo(BigDecimal.ZERO) < 0)
                throw new NotEnoughBalanceException("Not enough balance in account id " + oldAccount.getId() + "!");
            oldAccount.setBalance(balance);
            accountRepository.save(oldAccount);

            newAccount.setBalance(newAccount.getBalance().add(transactionIncomeDTO.getBalance()));
            transaction.setToAccount(accountRepository.save(newAccount));
        }

        transaction.setBalance(transactionIncomeDTO.getBalance());
        transaction.setDescription(transactionIncomeDTO.getDescription());
        transaction.setUser(userRepository.findByEmail(userEmail));
        transaction.setDeleted(false);
        transactionRepository.save(transaction);
        return ResponseEntity.ok().body(transaction);
    }

    @Override
    public ResponseEntity<Transaction> updateExpenseById(TransactionExpenseDTO transactionExpenseDTO, Long id, String userEmail) {
        Transaction transaction = transactionRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction id " + id + " not found!"));
        if (transaction.isDeleted())
            throw new ResourceNotFoundException("Transaction id " + id + " was deleted!");
        if (!transaction.getAction().equals("EXPENSE"))
            throw new ResourceNotFoundException("Transaction id " + id + " is NOT EXPENSE action!");

        List<Department> departmentList = userRepository.findByEmail(userEmail).getDepartments();
        if (!transaction.getUser().getRole().getName().equals("ROLE_ADMIN") && !departmentList.contains(transaction.getDepartment()))
            throw new AccessDenied("You do not have access to the next departmentId: " + transaction.getDepartment().getId());
        if (!transaction.getUser().getRole().getName().equals("ROLE_ADMIN") && !departmentList.contains(transactionExpenseDTO.getDepartment()))
            throw new AccessDenied("You do not have access to the next departmentId: " + transactionExpenseDTO.getDepartment());
        if (transaction.getDepartment().getId() != transactionExpenseDTO.getDepartment()){
            Department department = departmentService.getDepartmentById(transactionExpenseDTO.getDepartment()).getBody();
            transaction.setDepartment(department);
        }

        if (transaction.getCategory().getId() != transactionExpenseDTO.getCategory()){
            Category category = categoryService.getCategoryById(transactionExpenseDTO.getCategory()).getBody();
            transaction.setCategory(category);
        }
        if (transaction.getProject().getId() != transactionExpenseDTO.getProject()){
            Project project = projectService.getProjectById(transactionExpenseDTO.getProject()).getBody();
            transaction.setProject(project);
        }
        if (transaction.getCounterparty().getId() != transactionExpenseDTO.getCounterparty()){
            Counterparty counterparty = counterpartyService.getCounterpartyById(transactionExpenseDTO.getCounterparty()).getBody();
            transaction.setCounterparty(counterparty);
        }

        Account oldAccount = accountService.getAccountById(transaction.getFromAccount().getId()).getBody();
        Account newAccount = accountService.getAccountById(transactionExpenseDTO.getFromAccount()).getBody();

        BigDecimal balance;
        if (oldAccount.getId() == newAccount.getId()){
            balance = oldAccount.getBalance().add(transaction.getBalance()).subtract(transactionExpenseDTO.getBalance());
            if (balance.compareTo(BigDecimal.ZERO) < 0)
                throw new NotEnoughBalanceException("Not enough balance in account id " + oldAccount.getId() + "!");
            oldAccount.setBalance(balance);
            transaction.setFromAccount(accountRepository.save(oldAccount));
        } else {
            balance = newAccount.getBalance().subtract(transactionExpenseDTO.getBalance());
            if (balance.compareTo(BigDecimal.ZERO) < 0)
                throw new NotEnoughBalanceException("Not enough balance in account id " + newAccount.getId() + "!");
            newAccount.setBalance(balance);
            transaction.setFromAccount(accountRepository.save(newAccount));

            oldAccount.setBalance(oldAccount.getBalance().add(transaction.getBalance()));
            accountRepository.save(oldAccount);
        }

        transaction.setBalance(transactionExpenseDTO.getBalance());
        transaction.setDescription(transactionExpenseDTO.getDescription());
        transaction.setUser(userRepository.findByEmail(userEmail));
        transaction.setDeleted(false);
        transactionRepository.save(transaction);
        return ResponseEntity.ok().body(transaction);
    }

    @Override
    public ResponseEntity<Transaction> updateRemittanceById(TransactionRemittanceDTO transactionRemittanceDTO, Long id, String userEmail) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Transaction id " + id + " not found!"));
        if (transaction.isDeleted())
            throw new ResourceNotFoundException("Transaction id " + id + " was deleted!");
        if (!transaction.getAction().equals("REMITTANCE"))
            throw new ResourceNotFoundException("Transaction id " + id + " is NOT REMITTANCE action!");

        Account oldFromAccount = accountService.getAccountById(transaction.getFromAccount().getId()).getBody();
        Account newFromAccount = accountService.getAccountById(transactionRemittanceDTO.getFromAccount()).getBody();
        Account oldToAccount = accountService.getAccountById(transaction.getToAccount().getId()).getBody();
        Account newToAccount = accountService.getAccountById(transactionRemittanceDTO.getToAccount()).getBody();

        BigDecimal balance;
        if (oldFromAccount.getId() == newFromAccount.getId()) {
            balance = oldFromAccount.getBalance().add(transaction.getBalance()).subtract(transactionRemittanceDTO.getBalance());
            if (balance.compareTo(BigDecimal.ZERO) < 0)
                throw new NotEnoughBalanceException("Not enough balance in account id " + oldFromAccount.getId() + "!");
            oldFromAccount.setBalance(balance);
            transaction.setFromAccount(accountRepository.save(oldFromAccount));

            if (oldToAccount == newToAccount) {
                balance = oldToAccount.getBalance().subtract(transaction.getBalance()).add(transactionRemittanceDTO.getBalance());
                if (balance.compareTo(BigDecimal.ZERO) < 0)
                    throw new NotEnoughBalanceException("Not enough balance in account id " + oldToAccount.getId() + "!");
                oldToAccount.setBalance(balance);
                transaction.setToAccount(accountRepository.save(oldToAccount));
            } else {
                balance = oldToAccount.getBalance().subtract(transaction.getBalance());
                if (balance.compareTo(BigDecimal.ZERO) < 0)
                    throw new NotEnoughBalanceException("Not enough balance in account id " + oldToAccount.getId() + "!");
                oldToAccount.setBalance(balance);
                accountRepository.save(oldToAccount);

                balance = newToAccount.getBalance().add(transactionRemittanceDTO.getBalance());
                newToAccount.setBalance(balance);
                transaction.setToAccount(accountRepository.save(newToAccount));
            }
        } else {
            balance = oldFromAccount.getBalance().add(transaction.getBalance());
            oldFromAccount.setBalance(balance);
            accountRepository.save(oldFromAccount);

            balance = newFromAccount.getBalance().subtract(transactionRemittanceDTO.getBalance());
            if (balance.compareTo(BigDecimal.ZERO) < 0)
                throw new NotEnoughBalanceException("Not enough balance in account id " + newFromAccount.getId() + "!");
            newFromAccount.setBalance(balance);
            transaction.setFromAccount(accountRepository.save(newFromAccount));

            if (oldToAccount == newToAccount) {
                balance = newToAccount.getBalance().subtract(transaction.getBalance()).add(transactionRemittanceDTO.getBalance());
                if (balance.compareTo(BigDecimal.ZERO) < 0)
                    throw new NotEnoughBalanceException("Not enough balance in account id " + newToAccount.getId() + "!");
                newToAccount.setBalance(balance);
                transaction.setToAccount(accountRepository.save(newToAccount));
            } else {
                balance = oldToAccount.getBalance().subtract(transaction.getBalance());
                if (balance.compareTo(BigDecimal.ZERO) < 0)
                    throw new NotEnoughBalanceException("Not enough balance in account id " + oldToAccount.getId() + "!");
                oldToAccount.setBalance(balance);
                accountRepository.save(oldToAccount);

                balance = newToAccount.getBalance().add(transactionRemittanceDTO.getBalance());
                newToAccount.setBalance(balance);
                transaction.setToAccount(accountRepository.save(newToAccount));
            }

        }

        transaction.setBalance(transactionRemittanceDTO.getBalance());
        transaction.setDescription(transactionRemittanceDTO.getDescription());
        transaction.setUser(userRepository.findByEmail(userEmail));
        transaction.setDeleted(false);
        transactionRepository.save(transaction);
        return ResponseEntity.ok().body(transaction);
    }

    @Override
    public ResponseMessage deleteTransactionById(Long id, String userEmail) {
        Transaction transaction;
        if (userRepository.findByEmail(userEmail).getRole().getName().equals("ROLE_ADMIN"))
            transaction = transactionService.getByIdForAdmin(id).getBody();
        else transaction = transactionService.getByIdForUser(id, userEmail).getBody();

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

        List<Transaction> output = new ArrayList<>();
        if (start <= end) {
            output = list.subList(start, end);
        }
        return new PageImpl<>(output, pageable, list.size());
    }
}
