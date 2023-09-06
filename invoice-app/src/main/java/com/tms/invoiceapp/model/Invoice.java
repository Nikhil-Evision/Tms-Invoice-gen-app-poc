package com.tms.invoiceapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Invoice_info")
public class Invoice {

    @Id
    @Column(name = "InvoiceNumber")
    private String invoiceNumber;

    @Column(name = "InvoiceDate")
    private LocalDate invoiceDate;

    @Column(name = "ReverseCharge")
    private double reverseCharge;

    @Column(name = "State")
    private String state;

    @Column(name="StateCode")
    private String stateCode;

    @Column(name = "BillingPeriod")
    private LocalDate billingPeriod;

    @Column(name = "PlaceOfSupply")
    private String placeOfSupply;

    @Column(name = "VehicleNumber")
    private String vehicleNumber;

    @Column(name = "TransportMode")
    private String transportMode;

}
