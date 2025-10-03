package com.erp.inventory.system.dto;

import lombok.Data;

@Data
public class SupplierDto {
    private Long id;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String apiUrl;
}