package com.erp.inventory.system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String contactPerson;
    private String email;
    private String phone;
    private String apiUrl;

    @OneToMany(mappedBy = "supplier")
    private Set<Product> products;

    @OneToMany(mappedBy = "supplier")
    private Set<PurchaseOrder> purchaseOrders;
}
