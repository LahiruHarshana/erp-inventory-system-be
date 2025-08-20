package com.erp.inventory.system.dto;

import lombok.Data;

@Data
public class StoreDto {
    private Long id;
    private String storeName;
    private String storeRegistrationNumber;
    private String taxId;
    private String operationalDetails;
    private boolean isVerified;
}
