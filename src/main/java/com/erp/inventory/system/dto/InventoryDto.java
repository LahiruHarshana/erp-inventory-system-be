package com.erp.inventory.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryDto {
    private Long id;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;
    private Integer reorderPoint;
    private LocalDateTime lastUpdated;
}
