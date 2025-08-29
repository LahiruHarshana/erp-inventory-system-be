package com.erp.inventory.system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PlaceSalesOrderDto {
    @NotNull
    private Long buyerStoreId;
    private Long sellerStoreId;
    @NotNull
    private Long warehouseId;
    @NotNull
    @Size(min = 1)
    private List<SalesOrderItemDto> orderItems;
}
