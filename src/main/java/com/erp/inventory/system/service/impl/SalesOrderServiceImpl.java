package com.erp.inventory.system.service.impl;


import com.erp.inventory.system.dto.PaymentDto;
import com.erp.inventory.system.dto.PlaceSalesOrderDto;
import com.erp.inventory.system.dto.SalesOrderDto;
import com.erp.inventory.system.dto.SalesOrderItemDto;
import com.erp.inventory.system.model.*;
import com.erp.inventory.system.repository.*;
import com.erp.inventory.system.service.SalesOrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public SalesOrderDto placeSalesOrder(@NotNull @Valid PlaceSalesOrderDto placeSalesOrderDto) {
        if (placeSalesOrderDto.getOrderItems() == null || placeSalesOrderDto.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Sales order must contain at least one item.");
        }

        Store buyerStore = storeRepository.findById(placeSalesOrderDto.getBuyerStoreId())
                .orElseThrow(() -> new EntityNotFoundException("Buyer store not found with id: " + placeSalesOrderDto.getBuyerStoreId()));

//        Store sellerStore = storeRepository.findById(placeSalesOrderDto.getSellerStoreId())
//                .orElseThrow(() -> new EntityNotFoundException("Seller store not found with id: " + placeSalesOrderDto.getSellerStoreId()));

//        if (!sellerStore.isVerified()) {
//            throw new IllegalStateException("Cannot place order from unverified seller store: " + sellerStore.getStoreName());
//        }

        Warehouse warehouse = warehouseRepository.findById(placeSalesOrderDto.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + placeSalesOrderDto.getWarehouseId()));

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setBuyerStore(buyerStore);
//        salesOrder.setSellerStore(sellerStore);
        salesOrder.setWarehouse(warehouse);
        salesOrder.setOrderDate(LocalDateTime.now());
        salesOrder.setStatus(SalesStatus.PENDING);

        List<SalesOrderItem> orderItems = placeSalesOrderDto.getOrderItems().stream()
                .map(itemDto -> {
                    if (itemDto.getQuantity() <= 0) {
                        throw new IllegalArgumentException("Quantity must be positive for product ID: " + itemDto.getProductId());
                    }
                    Product product = productRepository.findById(itemDto.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemDto.getProductId()));

                    Inventory inventory = inventoryRepository.findByProductAndWarehouse(product, warehouse);
                    if (inventory == null || inventory.getQuantity() < itemDto.getQuantity()) {
                        throw new IllegalStateException("Insufficient stock for product: " + product.getName() + " in warehouse: " + warehouse.getName());
                    }

                    SalesOrderItem orderItem = new SalesOrderItem();
                    orderItem.setProduct(product);
                    orderItem.setQuantity(itemDto.getQuantity());
                    orderItem.setUnitPrice(BigDecimal.valueOf(product.getUnitPrice()));
                    orderItem.setSalesOrder(salesOrder);

                    // Deduct inventory
                    inventory.setQuantity(inventory.getQuantity() - itemDto.getQuantity());
                    inventory.setLastUpdated(LocalDateTime.now());
                    inventoryRepository.save(inventory);

                    return orderItem;
                })
                .collect(Collectors.toList());

        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        salesOrder.setOrderItems(orderItems);
        salesOrder.setTotalAmount(totalAmount);

        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);
        return mapToDto(savedOrder);
    }

    @Override
    @Transactional
    public void shipSalesOrder(Long orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Sales order not found with id: " + orderId));

        if (order.getStatus() != SalesStatus.PENDING) {
            throw new IllegalStateException("Order must be in PENDING status to be shipped. Current status: " + order.getStatus());
        }

        order.setStatus(SalesStatus.SHIPPED);
        salesOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void processPayment(Long orderId, @NotNull @Valid PaymentDto paymentDto) {
        if (!StringUtils.hasText(paymentDto.getTransactionId())) {
            throw new IllegalArgumentException("Transaction ID cannot be empty.");
        }
        if (!StringUtils.hasText(paymentDto.getPaymentMethod())) {
            throw new IllegalArgumentException("Payment method cannot be empty.");
        }

        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Sales order not found with id: " + orderId));

        if (order.getPayment() != null) {
            throw new IllegalStateException("Payment already exists for order ID: " + orderId);
        }

        if (order.getStatus() != SalesStatus.SHIPPED) {
            throw new IllegalStateException("Order must be in SHIPPED status to process payment. Current status: " + order.getStatus());
        }

        Payment payment = new Payment();
        payment.setSalesOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionId(paymentDto.getTransactionId());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());

        paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public SalesOrderDto getSalesOrderById(Long id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales order not found with id: " + id));
        return mapToDto(salesOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrderDto> getAllSalesOrders() {
        return salesOrderRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SalesOrderDto updateSalesOrderStatus(Long id, SalesStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Sales order status cannot be null.");
        }
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales order not found with id: " + id));

        if (status == SalesStatus.SHIPPED) {
            throw new IllegalStateException("Use shipSalesOrder to set status to SHIPPED.");
        }

        salesOrder.setStatus(status);
        SalesOrder updatedOrder = salesOrderRepository.save(salesOrder);
        return mapToDto(updatedOrder);
    }

    private SalesOrderDto mapToDto(SalesOrder order) {
        SalesOrderDto dto = new SalesOrderDto();
        dto.setId(order.getId());
        dto.setBuyerStoreId(order.getBuyerStore().getId());
        dto.setBuyerStoreName(order.getBuyerStore().getStoreName());
//        dto.setSellerStoreId(order.getSellerStore().getId());
//        dto.setSellerStoreName(order.getSellerStore().getStoreName());
        dto.setWarehouseId(order.getWarehouse().getId());
        dto.setWarehouseName(order.getWarehouse().getName());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::mapItemToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private SalesOrderItemDto mapItemToDto(SalesOrderItem item) {
        SalesOrderItemDto dto = new SalesOrderItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        return dto;
    }
}