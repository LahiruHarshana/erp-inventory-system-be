package com.erp.inventory.system.dto;

import com.erp.inventory.system.model.SalesStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SalesOrderDto {
    private Long id;
    private Long buyerStoreId;
    private String buyerStoreName;
    private Long sellerStoreId;
    private String sellerStoreName;
    private Long warehouseId;
    private String warehouseName;
    private LocalDateTime orderDate;
    private SalesStatus status;
    private BigDecimal totalAmount;
    private List<SalesOrderItemDto> orderItems;
}