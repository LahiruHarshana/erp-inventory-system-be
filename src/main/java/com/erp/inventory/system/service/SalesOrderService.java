package com.erp.inventory.system.service;

import com.erp.inventory.system.dto.PaymentDto;
import com.erp.inventory.system.dto.PlaceSalesOrderDto;
import com.erp.inventory.system.dto.SalesOrderDto;
import com.erp.inventory.system.model.SalesStatus;

import java.util.List;

public interface SalesOrderService {
    SalesOrderDto placeSalesOrder(PlaceSalesOrderDto placeSalesOrderDto);
    void shipSalesOrder(Long orderId);
    void processPayment(Long orderId, PaymentDto paymentDto);
    SalesOrderDto getSalesOrderById(Long id);
    List<SalesOrderDto> getAllSalesOrders();
    SalesOrderDto updateSalesOrderStatus(Long id, SalesStatus status);
}