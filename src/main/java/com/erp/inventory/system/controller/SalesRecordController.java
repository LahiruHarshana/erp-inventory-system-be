package com.erp.inventory.system.controller;

import com.erp.inventory.system.dto.SalesRecordDto;
import com.erp.inventory.system.service.SalesRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales-records")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "10. Sales Record Management", description = "APIs for retrieving sales record data")
public class SalesRecordController {

    private final SalesRecordService salesRecordService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN', 'ROLE_INVENTORY_MANAGER')")
    @Operation(summary = "Get sales records", description = "Retrieves sales records for a date range with store, product, and sales data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sales records retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<SalesRecordDto>> getSalesRecords(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//        ResponseEntity.ok(salesRecordService.getSalesRecords(startDate, endDate));
        System.out.println(ResponseEntity.ok(salesRecordService.getSalesRecords(startDate, endDate)));
        return ResponseEntity.ok(salesRecordService.getSalesRecords(startDate, endDate));
    }
}