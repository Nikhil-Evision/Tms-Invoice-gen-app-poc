package com.tms.invoiceapp.service;

import com.tms.invoiceapp.model.Services;
import com.tms.invoiceapp.repository.ServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicesService {
    @Autowired
    private ServicesRepository servicesRepository;

    public Services getServicesInfo(int serviceId){
        return servicesRepository.findById(serviceId).get();
    }
}
