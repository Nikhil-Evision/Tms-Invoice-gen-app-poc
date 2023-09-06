package com.tms.invoiceapp.service;

import com.tms.invoiceapp.model.Consignee;
import com.tms.invoiceapp.repository.ConsigneeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsigneeService {
    @Autowired
    private ConsigneeRepository consigneeRepository;

    public Consignee getConsigneeInfo(int consigneeId){
        return consigneeRepository.findById(consigneeId).get();
    }
}
