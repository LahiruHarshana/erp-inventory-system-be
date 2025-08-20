package com.erp.inventory.system.controller;

import com.erp.inventory.system.dto.InventoryDto;
import com.erp.inventory.system.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "7. Inventory Management", description = "APIs for managing stock levels")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/stock")
    @PreAuthorize("hasAnyAuthority('ROLE_INVENTORY_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<InventoryDto> updateStock(@RequestBody InventoryDto inventoryDto) {
        return ResponseEntity.ok(inventoryService.updateStock(inventoryDto));
    }

    @GetMapping("/stock")
    @PreAuthorize("hasAnyAuthority('ROLE_INVENTORY_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<InventoryDto> getStock(@RequestParam Long productId, @RequestParam Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getStock(productId, warehouseId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAnyAuthority('ROLE_INVENTORY_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<List<InventoryDto>> getInventoryByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouse(warehouseId));
    }
}
