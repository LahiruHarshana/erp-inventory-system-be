package com.erp.inventory.system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesOrderItemDto {
    private Long id;
    @NotNull
    private Long productId;
    private String productName;
    @NotNull
    @Min(1)
    private Integer quantity;
    private BigDecimal unitPrice;
}
