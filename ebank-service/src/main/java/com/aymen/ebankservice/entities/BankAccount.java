package com.aymen.ebankservice.entities;

import com.aymen.ebankservice.model.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.*;

import java.util.Date;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BankAccount {
    @Id
    private String Id;
    private Date createdAt;
    private double balance;
    private String accType;
    private long customerId;
    @Transient
    private Customer customer;
}
