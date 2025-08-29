package com.erp.inventory.system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalesRecordDto {
    private LocalDate date;
    private String storeId;
    private String productId;
    private String category;
    private String region;
    private Integer inventoryLevel; // Inventory.quantity
    private Integer unitsSold; // Sum of SalesOrderItem.quantity
    private Integer unitsOrdered; // Sum of PurchaseOrderItem.quantity
    private BigDecimal price; // Product.unitPrice or SalesOrderItem.unitPrice
    private BigDecimal discount; // Mock
    private String weatherCondition; // Mock
    private Integer holidayPromotion; // Mock
    private BigDecimal competitorPricing; // Mock
    private String seasonality; // Mock
}