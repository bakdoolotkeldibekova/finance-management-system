package com.example.fms.repository;

import com.example.fms.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByNameContainingIgnoringCase(String name);
    List<Account> findAllByBalanceLessThanEqual(BigDecimal balance);
    List<Account> findAllByBalanceGreaterThanEqual(BigDecimal balance);
    List<Account> findAllByDateCreatedBefore(LocalDateTime before);
    List<Account> findAllByDateCreatedAfter(LocalDateTime after);
}
