package com.example.fms.repository;

import com.example.fms.entity.Counterparty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CounterpartyRepository extends JpaRepository<Counterparty, Long> {
    List<Counterparty> findAllByOrderByDateCreatedDesc();
    List<Counterparty> findAllByNameContainingIgnoringCase(String name);
    List<Counterparty> findAllByDateCreatedBefore(LocalDateTime before);
    List<Counterparty> findAllByDateCreatedAfter(LocalDateTime after);
}
