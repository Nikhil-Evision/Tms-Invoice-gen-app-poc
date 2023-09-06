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
@Table(name = "Service_info")
public class Services {

    @Id
    @Column(name = "ServiceId")
    private int serviceId;

    @Column(name = "ResourceName")
    private String resourceName;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "MonthlyRate")
    private double monthlyRate;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "description")
    private String description;

    @Column(name = "HsnCode")
    private String hsnCode;
}
