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
@Table(name = "Consignee_info")
public class Consignee {

    @Id
    @Column(name = "ConsigneeId")
    private int consigneeId;

    @Column(name = "consigneeName")
    private String consigneeName;

    @Column(name = "ConsigneeAddress")
    private String consigneeAddress;

    @Column(name = "ConsigneeGstin")
    private String consigneeGstin;

    @Column(name = "ConsigneeCountry")
    private String consigneeCountry;


}
