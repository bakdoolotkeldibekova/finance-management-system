package com.example.fms.service;

import com.example.fms.dto.CounterpartyDTO;
import com.example.fms.entity.Counterparty;
import com.example.fms.entity.ResponseMessage;
import com.example.fms.entity.User;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface CounterpartyService {
    List<Counterparty> getAll();
    List<Counterparty> getAllByNameContaining(String name);
    List<Counterparty> getAllByDateCreatedBefore(String before);
    List<Counterparty> getAllByDateCreatedAfter(String after);

    ResponseEntity<Counterparty> addCounterparty (CounterpartyDTO counterpartyDTO, String userEmail);

    ResponseEntity<Counterparty> getCounterpartyById(Long id);

    ResponseEntity<Counterparty> updateCounterpartyById(CounterpartyDTO counterpartyDTO, Long id, String userEmail);

    ResponseMessage deleteCounterpartyById(Long id, String userEmail);
}
