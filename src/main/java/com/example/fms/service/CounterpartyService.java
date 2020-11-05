package com.example.fms.service;

import com.example.fms.entity.Counterparty;
import com.example.fms.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CounterpartyService {
    List<Counterparty> getAll();
    List<Counterparty> getAllByNameContaining(String name);
    List<Counterparty> getAllByDateCreatedBefore(String before);
    List<Counterparty> getAllByDateCreatedAfter(String after);

    Counterparty addCounterparty (Counterparty newCounterparty, String userEmail);

    Counterparty getCounterpartyById(Long id) throws Exception;

    Counterparty updateCounterpartyById(Counterparty newCounterparty, String userEmail) throws Exception;

    boolean deleteCounterpartyById(Long id, String userEmail);
}
