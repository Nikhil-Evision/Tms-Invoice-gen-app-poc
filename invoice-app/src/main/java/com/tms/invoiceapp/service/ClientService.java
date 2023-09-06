package com.tms.invoiceapp.service;

import com.tms.invoiceapp.model.Client;
import com.tms.invoiceapp.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public Client getClientInfo(int clientId){
        return clientRepository.findById(clientId).get();
    }


}
