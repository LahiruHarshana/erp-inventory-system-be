package com.erp.inventory.system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InventorySummaryDto {
    private int totalProducts;
    private int totalStockQuantity;
    private BigDecimal totalStockValue;
    private List<WarehouseSummaryDto> productsByWarehouse;

    @Data
    public static class WarehouseSummaryDto {
        private String warehouseName;
        private int productCount;
        private int totalQuantity;
    }
}
