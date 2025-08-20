package com.erp.inventory.system.service.impl;

import com.erp.inventory.system.dto.PurchaseOrderDto;
import com.erp.inventory.system.dto.PurchaseOrderItemDto;
import com.erp.inventory.system.model.*;
import com.erp.inventory.system.repository.ProductRepository;
import com.erp.inventory.system.repository.PurchaseOrderRepository;
import com.erp.inventory.system.repository.SupplierRepository;
import com.erp.inventory.system.service.PurchaseOrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public PurchaseOrderDto createPurchaseOrder(PurchaseOrderDto purchaseOrderDto) {
        Supplier supplier = supplierRepository.findById(purchaseOrderDto.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + purchaseOrderDto.getSupplierId()));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setOrderDate(LocalDateTime.now());
        purchaseOrder.setExpectedDeliveryDate(purchaseOrderDto.getExpectedDeliveryDate());
        purchaseOrder.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<PurchaseOrderItem> orderItems = purchaseOrderDto.getOrderItems().stream().map(itemDto -> {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemDto.getProductId()));

            PurchaseOrderItem orderItem = new PurchaseOrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(BigDecimal.valueOf(product.getUnitPrice())); // Capture price at time of order
            orderItem.setPurchaseOrder(purchaseOrder);

            // Add item total to the order's total amount
            BigDecimal itemTotal = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            // Using a non-final variable in a lambda requires it to be effectively final.
            // A better approach is to calculate the total after the stream.
            return orderItem;
        }).collect(Collectors.toList());

        // Calculate total amount after creating all items
        for(PurchaseOrderItem item : orderItems) {
            totalAmount = totalAmount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        purchaseOrder.setOrderItems(orderItems);
        purchaseOrder.setTotalAmount(totalAmount);

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);
        return mapToDto(savedOrder);
    }

    @Override
    public PurchaseOrderDto getPurchaseOrderById(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase Order not found with id: " + id));
        return mapToDto(purchaseOrder);
    }

    @Override
    public List<PurchaseOrderDto> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public PurchaseOrderDto updateOrderStatus(Long id, OrderStatus status) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase Order not found with id: " + id));
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
        dto.setOrderItems(order.getOrderItems().stream().map(this::mapItemToDto).collect(Collectors.toList()));
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