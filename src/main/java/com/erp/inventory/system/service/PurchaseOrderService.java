package com.erp.inventory.system.service;

import com.erp.inventory.system.dto.PaymentDto;
import com.erp.inventory.system.dto.PurchaseOrderDto;
import com.erp.inventory.system.model.OrderStatus;

import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderDto createPurchaseOrder(PurchaseOrderDto purchaseOrderDto);
    PurchaseOrderDto getPurchaseOrderById(Long id);
    List<PurchaseOrderDto> getAllPurchaseOrders();
    PurchaseOrderDto updateOrderStatus(Long id, OrderStatus status);
    void receivePurchaseOrder(Long orderId, Long warehouseId);
    void makePayment(Long orderId, PaymentDto paymentDto);
}