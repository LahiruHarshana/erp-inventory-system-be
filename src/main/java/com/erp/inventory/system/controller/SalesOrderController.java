package com.erp.inventory.system.controller;

import com.erp.inventory.system.dto.PaymentDto;
import com.erp.inventory.system.dto.PlaceSalesOrderDto;
import com.erp.inventory.system.dto.SalesOrderDto;
import com.erp.inventory.system.model.SalesStatus;
import com.erp.inventory.system.service.SalesOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales-orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "9. Sales Order Management", description = "APIs for managing sales orders")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN')")
    @Operation(summary = "Place a new sales order", description = "Creates a sales order for a buyer store, deducting stock from the specified warehouse.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sales order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient stock"),
            @ApiResponse(responseCode = "404", description = "Store, warehouse, or product not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<SalesOrderDto> placeSalesOrder(@RequestBody PlaceSalesOrderDto placeSalesOrderDto) {
        return new ResponseEntity<>(salesOrderService.placeSalesOrder(placeSalesOrderDto), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/ship")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER')")
    @Operation(summary = "Ship a sales order", description = "Marks a sales order as shipped.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order shipped successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Order not in PENDING status")
    })
    public ResponseEntity<Void> shipSalesOrder(@PathVariable Long id) {
        salesOrderService.shipSalesOrder(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/payment")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN')")
    @Operation(summary = "Process payment for a sales order", description = "Records payment for a shipped sales order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Order not in SHIPPED status or payment already exists")
    })
    public ResponseEntity<Void> processPayment(@PathVariable Long id, @RequestBody PaymentDto paymentDto) {
        salesOrderService.processPayment(id, paymentDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER')")
    @Operation(summary = "Get sales order by ID", description = "Retrieves a specific sales order by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<SalesOrderDto> getSalesOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(salesOrderService.getSalesOrderById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER')")
    @Operation(summary = "Get all sales orders", description = "Retrieves a list of all sales orders.")
    @ApiResponse(responseCode = "200", description = "List of orders retrieved successfully")
    public ResponseEntity<List<SalesOrderDto>> getAllSalesOrders() {
        return ResponseEntity.ok(salesOrderService.getAllSalesOrders());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN')")
    @Operation(summary = "Update sales order status", description = "Updates the status of a sales order (e.g., PENDING, CANCELLED).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status")
    })
    public ResponseEntity<SalesOrderDto> updateSalesOrderStatus(@PathVariable Long id, @RequestParam SalesStatus status) {
        return ResponseEntity.ok(salesOrderService.updateSalesOrderStatus(id, status));
    }
}