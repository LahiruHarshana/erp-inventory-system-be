package com.erp.inventory.system.dto;

import com.erp.inventory.system.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderDto {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private LocalDateTime orderDate;
    private LocalDate expectedDeliveryDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<PurchaseOrderItemDto> orderItems;
}
