package com.erp.inventory.system.service.impl;

import com.erp.inventory.system.dto.PaymentDto;
import com.erp.inventory.system.dto.PurchaseOrderDto;
import com.erp.inventory.system.dto.PurchaseOrderItemDto;
import com.erp.inventory.system.model.*;
import com.erp.inventory.system.repository.*;
import com.erp.inventory.system.service.PurchaseOrderService;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public PurchaseOrderDto createPurchaseOrder(@NotNull @Valid PurchaseOrderDto purchaseOrderDto) {
        if (purchaseOrderDto.getOrderItems() == null || purchaseOrderDto.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Purchase order must contain at least one item.");
        }

        Supplier supplier = supplierRepository.findById(purchaseOrderDto.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + purchaseOrderDto.getSupplierId()));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setOrderDate(LocalDateTime.now());
        purchaseOrder.setExpectedDeliveryDate(purchaseOrderDto.getExpectedDeliveryDate());
        purchaseOrder.setStatus(OrderStatus.PENDING);

        List<PurchaseOrderItem> orderItems = purchaseOrderDto.getOrderItems().stream()
                .map(itemDto -> {
                    if (itemDto.getQuantity() <= 0) {
                        throw new IllegalArgumentException("Quantity must be positive for product ID: " + itemDto.getProductId());
                    }
                    Product product = productRepository.findById(itemDto.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemDto.getProductId()));

                    PurchaseOrderItem orderItem = new PurchaseOrderItem();
                    orderItem.setProduct(product);
                    orderItem.setQuantity(itemDto.getQuantity());
                    orderItem.setUnitPrice(BigDecimal.valueOf(product.getUnitPrice()));
                    orderItem.setPurchaseOrder(purchaseOrder);
                    return orderItem;
                })
                .collect(Collectors.toList());

        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        purchaseOrder.setOrderItems(orderItems);
        purchaseOrder.setTotalAmount(totalAmount);

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);
        return mapToDto(savedOrder);
    }

    @Override
    @Transactional
    public void receivePurchaseOrder(Long orderId, Long warehouseId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order must be in PENDING status to be received. Current status: " + order.getStatus());
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + warehouseId));

        for (PurchaseOrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            Inventory inventory = inventoryRepository.findByProductAndWarehouse(product, warehouse);

            if (inventory == null) {
                inventory = new Inventory();
                inventory.setProduct(product);
                inventory.setWarehouse(warehouse);
                inventory.setQuantity(0);
                inventory.setReorderPoint(10); // Default; configure as needed
            }

            int newQuantity = inventory.getQuantity() + item.getQuantity();
            if (newQuantity < 0) {
                throw new IllegalStateException("Cannot reduce inventory below zero for product: " + product.getName());
            }

            inventory.setQuantity(newQuantity);
            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);
        }

        order.setStatus(OrderStatus.RECEIVED);
        purchaseOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void makePayment(Long orderId, @NotNull @Valid PaymentDto paymentDto) {
        if (!StringUtils.hasText(paymentDto.getTransactionId())) {
            throw new IllegalArgumentException("Transaction ID cannot be empty.");
        }
        if (!StringUtils.hasText(paymentDto.getPaymentMethod())) {
            throw new IllegalArgumentException("Payment method cannot be empty.");
        }

        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with id: " + orderId));

        if (order.getPayment() != null) {
            throw new IllegalStateException("Payment already exists for order ID: " + orderId);
        }

        if (order.getStatus() != OrderStatus.RECEIVED) {
            throw new IllegalStateException("Order must be in RECEIVED status to process payment. Current status: " + order.getStatus());
        }

        Payment payment = new Payment();
        payment.setPurchaseOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionId(paymentDto.getTransactionId());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());

        paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderDto getPurchaseOrderById(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with id: " + id));
        return mapToDto(purchaseOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PurchaseOrderDto updateOrderStatus(Long id, OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null.");
        }
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with id: " + id));

        // Prevent manual status changes that bypass workflow (e.g., setting to RECEIVED without inventory update)
        if (status == OrderStatus.RECEIVED) {
            throw new IllegalStateException("Use receivePurchaseOrder to set status to RECEIVED to ensure inventory updates.");
        }

        purchaseOrder.setStatus(status);
        PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
        return mapToDto(updatedOrder);
    }

    private PurchaseOrderDto mapToDto(PurchaseOrder order) {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(order.getId());
        dto.setSupplierId(order.getSupplier().getId());
        dto.setSupplierName(order.getSupplier().getName());
        dto.setOrderDate(order.getOrderDate());
        dto.setExpectedDeliveryDate(order.getExpectedDeliveryDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::mapItemToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private PurchaseOrderItemDto mapItemToDto(PurchaseOrderItem item) {
        PurchaseOrderItemDto dto = new PurchaseOrderItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        return dto;
    }
}