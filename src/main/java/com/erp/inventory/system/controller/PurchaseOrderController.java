package com.erp.inventory.system.controller;

import com.erp.inventory.system.dto.PaymentDto;
import com.erp.inventory.system.dto.PurchaseOrderDto;
import com.erp.inventory.system.model.OrderStatus;
import com.erp.inventory.system.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@CrossOrigin(origins = "*")
@Tag(name = "8. Purchase Order Management", description = "APIs for managing purchase orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN','ROLE_BUSINESS_OWNER')")
    public ResponseEntity<PurchaseOrderDto> createPurchaseOrder(@RequestBody PurchaseOrderDto purchaseOrderDto) {
        return new ResponseEntity<>(purchaseOrderService.createPurchaseOrder(purchaseOrderDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER','ROLE_BUSINESS_OWNER')")
    public ResponseEntity<PurchaseOrderDto> getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER','ROLE_BUSINESS_OWNER')")
    public ResponseEntity<List<PurchaseOrderDto>> getAllPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN','ROLE_BUSINESS_OWNER')")
    public ResponseEntity<PurchaseOrderDto> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(purchaseOrderService.updateOrderStatus(id, status));
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER','ROLE_BUSINESS_OWNER')")
    @Operation(summary = "Receive a purchase order", description = "Marks a purchase order as received and updates inventory quantities in the specified warehouse.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchase order received and inventory updated successfully"),
            @ApiResponse(responseCode = "404", description = "Purchase order or warehouse not found"),
            @ApiResponse(responseCode = "400", description = "Order not in PENDING status")
    })
    public ResponseEntity<Void> receivePurchaseOrder(@PathVariable Long id, @RequestParam Long warehouseId) {
        purchaseOrderService.receivePurchaseOrder(id, warehouseId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/payment")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN','ROLE_BUSINESS_OWNER')")
    @Operation(summary = "Process payment for a purchase order", description = "Creates a payment record for a received purchase order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found"),
            @ApiResponse(responseCode = "400", description = "Order not in RECEIVED status or payment already exists")
    })
    public ResponseEntity<Void> makePayment(@PathVariable Long id, @RequestBody PaymentDto paymentDto) {
        purchaseOrderService.makePayment(id, paymentDto);
        return ResponseEntity.ok().build();
    }
}