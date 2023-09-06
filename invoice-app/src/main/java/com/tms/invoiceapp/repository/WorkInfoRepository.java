package com.tms.invoiceapp.repository;

import com.tms.invoiceapp.model.WorkInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkInfoRepository extends JpaRepository<WorkInfo,Integer>
{

}
