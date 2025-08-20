package com.erp.inventory.system.dto;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private Long categoryId;
    private Long supplierId;
    private double unitPrice;
}
