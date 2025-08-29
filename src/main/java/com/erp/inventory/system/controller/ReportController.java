package com.erp.inventory.system.controller;

import com.erp.inventory.system.dto.InventorySummaryDto;
import com.erp.inventory.system.dto.LowStockItemDto;
import com.erp.inventory.system.dto.PurchaseOrderDto;
import com.erp.inventory.system.model.OrderStatus;
import com.erp.inventory.system.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "9. Report Management", description = "APIs for generating inventory and purchase order reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/inventory-summary")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER','ROLE_BUSINESS_OWNER')")
    @Operation(summary = "Get inventory summary report", description = "Provides an overview of total stock quantity and value across all warehouses.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory summary retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<InventorySummaryDto> getInventorySummary() {
        return ResponseEntity.ok(reportService.getInventorySummary());
    }

    @GetMapping("/purchase-orders")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER','ROLE_BUSINESS_OWNER')")
    @Operation(summary = "Get purchase order history report", description = "Retrieves purchase orders filtered by date range, supplier, and status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchase order history retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<PurchaseOrderDto>> getPurchaseOrderHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) OrderStatus status) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
        return ResponseEntity.ok(reportService.getPurchaseOrderHistory(startDate, endDate, supplierId, status));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER','ROLE_BUSINESS_OWNER')")
    @Operation(summary = "Get low stock report", description = "Retrieves inventory items with quantities below the specified threshold.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Low stock report retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<LowStockItemDto>> getLowStockReport(@RequestParam(required = false) Integer threshold) {
        if (threshold != null && threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative.");
        }
        return ResponseEntity.ok(reportService.getLowStockReport(threshold));
    }
}