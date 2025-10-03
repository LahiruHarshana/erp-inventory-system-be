package com.erp.inventory.system.service;

import com.erp.inventory.system.dto.WarehouseDto;

import java.util.List;

public interface WarehouseService {
    WarehouseDto createWarehouse(WarehouseDto warehouseDto);
    WarehouseDto getWarehouseById(Long id);
    WarehouseDto updateWarehouse(Long id, WarehouseDto warehouseDto);
    void deleteWarehouse(Long id);

    List<WarehouseDto> getWarehouses();
}
