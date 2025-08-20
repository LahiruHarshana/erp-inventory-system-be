package com.erp.inventory.system.service.impl;

import com.erp.inventory.system.dto.WarehouseDto;
import com.erp.inventory.system.model.Store;
import com.erp.inventory.system.model.Warehouse;
import com.erp.inventory.system.repository.StoreRepository;
import com.erp.inventory.system.repository.WarehouseRepository;
import com.erp.inventory.system.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final StoreRepository storeRepository;

    @Override
    public WarehouseDto createWarehouse(WarehouseDto warehouseDto) {
        Store store = storeRepository.findById(warehouseDto.getStoreId())
                .orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + warehouseDto.getStoreId()));

        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseDto.getName());
        warehouse.setLocation(warehouseDto.getLocation());
        warehouse.setStore(store);

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return mapToDto(savedWarehouse);
    }

    @Override
    public WarehouseDto getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + id));
        return mapToDto(warehouse);
    }

    @Override
    public List<WarehouseDto> getAllWarehousesByStore(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new EntityNotFoundException("Store not found with id: " + storeId);
        }
        return warehouseRepository.findByStoreId(storeId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseDto updateWarehouse(Long id, WarehouseDto warehouseDto) {
        Warehouse existingWarehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + id));

        existingWarehouse.setName(warehouseDto.getName());
        existingWarehouse.setLocation(warehouseDto.getLocation());
        // Note: Changing the store of a warehouse might have complex implications, handled here simply.
        Store store = storeRepository.findById(warehouseDto.getStoreId())
                .orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + warehouseDto.getStoreId()));
        existingWarehouse.setStore(store);

        Warehouse updatedWarehouse = warehouseRepository.save(existingWarehouse);
        return mapToDto(updatedWarehouse);
    }

    @Override
    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new EntityNotFoundException("Warehouse not found with id: " + id);
        }
        warehouseRepository.deleteById(id);
    }

    private WarehouseDto mapToDto(Warehouse warehouse) {
        WarehouseDto dto = new WarehouseDto();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        dto.setStoreId(warehouse.getStore().getId());
        return dto;
    }
}
