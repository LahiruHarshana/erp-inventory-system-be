package com.erp.inventory.system.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderItemDto {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String productName;
}
