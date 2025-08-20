package com.erp.inventory.system.controller;

import com.erp.inventory.system.dto.PurchaseOrderDto;
import com.erp.inventory.system.model.OrderStatus;
import com.erp.inventory.system.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "8. Purchase Order Management", description = "APIs for managing purchase orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN')")
    public ResponseEntity<PurchaseOrderDto> createPurchaseOrder(@RequestBody PurchaseOrderDto purchaseOrderDto) {
        return new ResponseEntity<>(purchaseOrderService.createPurchaseOrder(purchaseOrderDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER')")
    public ResponseEntity<PurchaseOrderDto> getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER')")
    public ResponseEntity<List<PurchaseOrderDto>> getAllPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN')")
    public ResponseEntity<PurchaseOrderDto> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(purchaseOrderService.updateOrderStatus(id, status));
    }
}