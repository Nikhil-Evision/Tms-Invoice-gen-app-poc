package com.tms.invoiceapp.repository;

import com.tms.invoiceapp.model.Consignee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsigneeRepository extends JpaRepository<Consignee,Integer> {
}
