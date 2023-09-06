package com.tms.invoiceapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Client_Info")
public class Client {

    @Id
    @Column(name = "ClientId")
    private int clientId;

    @Column(name="ClientName")
    private String clientName;

    @Column(name = "ClientAddress")
    private String clientAddress;

    @Column(name = "ClientCountry")
    private String clientCountry;

    @Column(name = "ClientGstin")
    private String clientGstin;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "isGstApplicable")
    private boolean isGstApplicable;

}
