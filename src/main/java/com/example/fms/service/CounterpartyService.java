package com.example.fms.service;

import com.example.fms.dto.CounterpartyDTO;
import com.example.fms.entity.Counterparty;
import com.example.fms.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CounterpartyService {
    List<Counterparty> getAll();
    List<Counterparty> getAllByNameContaining(String name);
    List<Counterparty> getAllByDateCreatedBefore(String before);
    List<Counterparty> getAllByDateCreatedAfter(String after);

    Counterparty addCounterparty (CounterpartyDTO counterpartyDTO, String userEmail);

    Counterparty getCounterpartyById(Long id);

    Counterparty updateCounterpartyById(CounterpartyDTO counterpartyDTO, Long id, String userEmail);

    Counterparty deleteCounterpartyById(Long id, String userEmail);
}
