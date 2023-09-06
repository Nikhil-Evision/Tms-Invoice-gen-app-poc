package com.tms.invoiceapp.repository;

import com.tms.invoiceapp.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicesRepository extends JpaRepository<Services,Integer> {
}
