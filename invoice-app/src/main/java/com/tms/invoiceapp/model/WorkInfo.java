package com.tms.invoiceapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Month;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "work_info")
public class WorkInfo {

    @Id
    @Column(name = "work_id")
    private int workId;

    @Column(name = "client_id")
    private int clientId;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "current_month")
    private Month currentMonth;

    @Column(name = "total_working_days")
    private double totalWorkingdays;

    @Column(name = "working_days")
    private double workingDays;

    @Column(name="leaves")
    private double leaves;

    @Column(name = "discount")
    private double discount;
}
