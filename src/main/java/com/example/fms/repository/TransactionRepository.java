package com.example.fms.repository;

import com.example.fms.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByOrderByDateCreatedDesc();
    List<Transaction> findAllByDeletedOrderByDateCreatedDesc(boolean isDeleted);
    List<Transaction> findAllByActionContainingIgnoringCase(String action);
    List<Transaction> findAllByDeletedAndActionContainingIgnoringCase(boolean isDeleted, String action);
    List<Transaction> findAllByUserId(Long userId);
    List<Transaction> findAllByFromAccountId(Long accountId);
    List<Transaction> findAllByToAccountId(Long accountId);
    List<Transaction> findAllByCategoryId(Long categoryId);
    List<Transaction> findAllByBalanceGreaterThanEqual(BigDecimal balance);
    List<Transaction> findAllByBalanceLessThanEqual(BigDecimal balance);
    List<Transaction> findAllByDateCreatedAfter(LocalDateTime after);
    List<Transaction> findAllByDateCreatedBefore(LocalDateTime before);
    List<Transaction> findAllByProjectId(Long projectId);
    List<Transaction> findAllByCounterpartyId(Long counterpartyId);
}
