package com.erp.inventory.system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sales_orders")
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_store_id", nullable = false)
    private Store buyerStore;


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "seller_store_id", nullable = false)
//    private Store sellerStore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private SalesStatus status;

    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private List<SalesOrderItem> orderItems;

    @OneToOne(mappedBy = "salesOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;
}