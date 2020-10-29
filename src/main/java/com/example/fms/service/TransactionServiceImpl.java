package com.example.fms.service;

import com.example.fms.dto.TransactionExpenseDTO;
import com.example.fms.dto.TransactionIncomeDTO;
import com.example.fms.dto.TransactionRemittanceDTO;
import com.example.fms.entity.*;

import com.example.fms.repository.AccountRepository;
import com.example.fms.repository.JournalRepository;
import com.example.fms.repository.StaffRepository;
import com.example.fms.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
//    @Autowired
//    private StaffRepository staffRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JournalRepository journalRepository;

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction addIncome(TransactionIncomeDTO transactionIncomeDTO, String userEmail) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAction("INCOME");
        transaction.setCategory(transactionIncomeDTO.getCategory());
        transaction.setCounterparty(transactionIncomeDTO.getCounterparty());
        transaction.setPrice(transactionIncomeDTO.getPrice());
        transaction.setProject(transactionIncomeDTO.getProject());
        transaction.setToAccount(transactionIncomeDTO.getToAccount());
        transaction.setDescription(transactionIncomeDTO.getDescription());
        transaction.setUser(userService.getByEmail(userEmail));

        Account account = accountRepository.findById(transaction.getToAccount().getId()).orElseThrow(Exception::new);
        account.setPrice(account.getPrice().add(transaction.getPrice()));
        accountRepository.save(account);
        /*
        if(transaction.getStaff() != null) {
            List<Staff> staffList = transaction.getStaff();
            for (Staff item:staffList) {
                Staff newStaff = staffRepository.findById(item.getId()).orElseThrow(Exception::new);
                newStaff.setAccepted(newStaff.getAccepted().subtract(transaction.getPrice()));
                staffRepository.save(newStaff);
            }
        }
        */
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction addExpense(TransactionExpenseDTO transactionExpenseDTO, String userEmail) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAction("EXPENSE");
        transaction.setCategory(transactionExpenseDTO.getCategory());
        transaction.setCounterparty(transactionExpenseDTO.getCounterparty());
        transaction.setPrice(transactionExpenseDTO.getPrice());
        transaction.setProject(transactionExpenseDTO.getProject());
        transaction.setFromAccount(transactionExpenseDTO.getFromAccount());
        transaction.setDescription(transactionExpenseDTO.getDescription());
        transaction.setUser(userService.getByEmail(userEmail));

        Account account = accountRepository.findById(transaction.getFromAccount().getId()).orElseThrow(Exception::new);
        account.setPrice(account.getPrice().subtract(transaction.getPrice()));
        accountRepository.save(account);

//        Staff staff = transaction.getStaff();
//        staff.setAccepted(staff.getAccepted().subtract(transaction.getPrice()));
//        staffRepository.save(staff);

        return transactionRepository.save(transaction);
    }
    @Override
    public Transaction addRemittance(TransactionRemittanceDTO transactionRemittanceDTO, String userEmail) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAction("REMITTANCE");
        transaction.setFromAccount(transactionRemittanceDTO.getFromAccount());
        transaction.setToAccount(transactionRemittanceDTO.getToAccount());
        transaction.setPrice(transactionRemittanceDTO.getPrice());
        transaction.setDescription(transactionRemittanceDTO.getDescription());
        transaction.setUser(userService.getByEmail(userEmail));

        Account accountFrom = accountRepository.findById(transaction.getFromAccount().getId()).orElseThrow(Exception::new);
        accountFrom.setPrice(accountFrom.getPrice().subtract(transaction.getPrice()));
        accountRepository.save(accountFrom);

        Account accountTo = accountRepository.findById(transaction.getToAccount().getId()).orElseThrow(Exception::new);
        accountTo.setPrice(accountTo.getPrice().add(transaction.getPrice()));
        accountRepository.save(accountTo);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransactionById(Long id) throws Exception {
        return transactionRepository.findById(id).orElseThrow(Exception::new);
    }

    @Override
    public List<Transaction> getByAction(String action) {
        return transactionRepository.findAllByActionContainingIgnoringCase(action);
    }

    @Override
    public List<Transaction> getByUserId(Long userId) {
        return transactionRepository.findAllByUserId(userId);
    }

    @Override
    public List<Transaction> getByAccountId(Long accountId) {
        List<Transaction> transactionList = transactionRepository.findAllByFromAccountId(accountId);
        transactionList.addAll(transactionRepository.findAllByToAccountId(accountId));
        return transactionList;
    }

    @Override
    public List<Transaction> getByDate(LocalDateTime after, LocalDateTime before) {
        return transactionRepository.findByDateCreatedAfterAndDateCreatedBefore(after, before);
    }

    @Override
    public List<Transaction> getByCategory(Long categoryId) {
        return transactionRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public Transaction updateTransactionById(Transaction newTransaction, String userEmail) throws Exception {
       return transactionRepository.findById(newTransaction.getId())
               .map(transaction -> {
                   transaction.setAction(newTransaction.getAction());
                   transaction.setFromAccount(newTransaction.getFromAccount());
                   transaction.setCategory(newTransaction.getCategory());
                   transaction.setToAccount(newTransaction.getToAccount());
                   transaction.setPrice(newTransaction.getPrice());
                   transaction.setProject(newTransaction.getProject());
                //   transaction.setStaff(newTransaction.getStaff());
                   transaction.setCounterparty(newTransaction.getCounterparty());
                   transaction.setDescription(newTransaction.getDescription());
                   transaction.setUser(userService.getByEmail(userEmail));
                   return transactionRepository.save(transaction);
               }).orElseThrow(Exception::new);
    }

    @Override
    public boolean deleteTransactionById(Long id, String userEmail) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        if (transaction != null){
            transactionRepository.deleteById(id);

            Journal journal = new Journal(); //всее действия сохр в транзакции, только del в журнале
            journal.setAction1("TRANSACTION: " + transaction.getAction());
            journal.setAction2("delete");
            journal.setUser(userService.getByEmail(userEmail));
            journalRepository.save(journal);
            return true;
        }
        return false;
    }
}
