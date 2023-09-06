package com.tms.invoiceapp.repository;

import com.tms.invoiceapp.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
