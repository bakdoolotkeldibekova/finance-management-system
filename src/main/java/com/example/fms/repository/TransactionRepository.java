package com.example.fms.repository;

import com.example.fms.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByActionContainingIgnoringCase(String action);
    List<Transaction> findAllByUserId(Long userId);

    List<Transaction> findAllByFromAccountId(Long accountId);
    List<Transaction> findAllByToAccountId(Long accountId);

    List<Transaction> findByDateCreatedAfterAndDateCreatedBefore(LocalDateTime after, LocalDateTime before);
    List<Transaction> findAllByCategoryId(Long categoryId);

}
