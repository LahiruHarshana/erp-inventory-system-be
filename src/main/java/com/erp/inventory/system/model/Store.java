package com.erp.inventory.system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String storeName;

    @Column(unique = true)
    private String storeRegistrationNumber;

    private String taxId;
    private String operationalDetails;
    private boolean isVerified = false;

    @OneToMany(mappedBy = "store")
    private Set<User> employees;
}