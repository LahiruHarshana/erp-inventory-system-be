package com.erp.inventory.system.dto;

import lombok.Data;

@Data
public class PaymentDto {
    private String transactionId;
    private String paymentMethod;
}