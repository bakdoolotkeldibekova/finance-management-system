package com.example.fms.service;

import com.example.fms.entity.Counterparty;
import com.example.fms.entity.User;

import java.util.List;

public interface CounterpartyService {
    List<Counterparty> getAll();

    Counterparty addCounterparty (Counterparty newCounterparty, String userEmail);

    Counterparty getCounterpartyById(Long id) throws Exception;

    Counterparty updateCounterpartyById(Counterparty newCounterparty, String userEmail) throws Exception;

    boolean deleteCounterpartyById(Long id, String userEmail);
}
