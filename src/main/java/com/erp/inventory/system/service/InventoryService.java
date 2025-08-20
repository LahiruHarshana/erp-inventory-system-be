package com.erp.inventory.system.service;

import com.erp.inventory.system.dto.InventoryDto;

import java.util.List;

public interface InventoryService {
    InventoryDto updateStock(InventoryDto inventoryDto);
    InventoryDto getStock(Long productId, Long warehouseId);
    List<InventoryDto> getInventoryByWarehouse(Long warehouseId);
}
