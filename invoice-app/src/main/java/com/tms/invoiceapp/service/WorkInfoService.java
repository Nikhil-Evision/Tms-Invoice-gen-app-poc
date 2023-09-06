package com.tms.invoiceapp.service;

import com.tms.invoiceapp.model.WorkInfo;
import com.tms.invoiceapp.repository.WorkInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkInfoService {
    @Autowired
    private WorkInfoRepository workInfoRepository;

    public WorkInfo getWorkInfo(int workId){
        return workInfoRepository.findById(workId).get();
    }
}
