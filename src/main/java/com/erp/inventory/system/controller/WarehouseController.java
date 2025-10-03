package com.erp.inventory.system.controller;

import com.erp.inventory.system.dto.WarehouseDto;
import com.erp.inventory.system.service.WarehouseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "6. Warehouse Management", description = "APIs for managing warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_INVENTORY_MANAGER', 'ROLE_ADMIN','ROLE_BUSINESS_OWNER')")
    public ResponseEntity<WarehouseDto> createWarehouse(@RequestBody WarehouseDto warehouseDto) {
        return new ResponseEntity<>(warehouseService.createWarehouse(warehouseDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_INVENTORY_MANAGER', 'ROLE_ADMIN','ROLE_BUSINESS_OWNER')")
    public ResponseEntity<WarehouseDto> getWarehouseById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_INVENTORY_MANAGER', 'ROLE_ADMIN','ROLE_SUPPLY_CHAIN_COORDINATOR','ROLE_BUSINESS_OWNER')")
    public ResponseEntity<List<WarehouseDto>> getWarehouses() {
        return ResponseEntity.ok(warehouseService.getWarehouses());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_INVENTORY_MANAGER', 'ROLE_ADMIN','ROLE_BUSINESS_OWNER')")
    public ResponseEntity<WarehouseDto> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseDto warehouseDto) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouseDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_INVENTORY_MANAGER', 'ROLE_ADMIN','ROLE_BUSINESS_OWNER')")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}

