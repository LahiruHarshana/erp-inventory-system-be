package com.erp.inventory.system.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PlaceOrderDto {
    @NotNull
    private Long supplierId;
    @NotNull
    private Long storeId;
    private LocalDate expectedDeliveryDate;
    @NotNull
    @Size(min = 1)
    private List<PurchaseOrderItemDto> orderItems;
}