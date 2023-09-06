package com.tms.invoiceapp.service;

import com.tms.invoiceapp.model.Invoice;
import com.tms.invoiceapp.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    public Invoice getInvoiceInfo(String invoiceId){
        return invoiceRepository.findById(invoiceId).get();
    }
}
