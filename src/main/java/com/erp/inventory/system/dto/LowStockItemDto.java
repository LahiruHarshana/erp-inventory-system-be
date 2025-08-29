package com.erp.inventory.system.dto;

import lombok.Data;

@Data
public class LowStockItemDto {
    private Long productId;
    private String productName;
    private String sku;
    private String warehouseName;
    private int currentQuantity;
    private String supplierName;
}