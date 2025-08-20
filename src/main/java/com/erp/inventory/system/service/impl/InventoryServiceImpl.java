package com.erp.inventory.system.service.impl;

import com.erp.inventory.system.dto.InventoryDto;
import com.erp.inventory.system.model.Inventory;
import com.erp.inventory.system.model.Product;
import com.erp.inventory.system.model.Warehouse;
import com.erp.inventory.system.repository.InventoryRepository;
import com.erp.inventory.system.repository.ProductRepository;
import com.erp.inventory.system.repository.WarehouseRepository;
import com.erp.inventory.system.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public InventoryDto updateStock(InventoryDto inventoryDto) {
        Inventory inventory = inventoryRepository
                .findByProductIdAndWarehouseId(inventoryDto.getProductId(), inventoryDto.getWarehouseId())
                .orElseGet(() -> {
                    Product product = productRepository.findById(inventoryDto.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + inventoryDto.getProductId()));
                    Warehouse warehouse = warehouseRepository.findById(inventoryDto.getWarehouseId())
                            .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + inventoryDto.getWarehouseId()));
                    Inventory newInventory = new Inventory();
                    newInventory.setProduct(product);
                    newInventory.setWarehouse(warehouse);
                    return newInventory;
                });

        inventory.setQuantity(inventoryDto.getQuantity());
        inventory.setReorderPoint(inventoryDto.getReorderPoint());
        inventory.setLastUpdated(LocalDateTime.now());

        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapToDto(savedInventory);
    }

    @Override
    public InventoryDto getStock(Long productId, Long warehouseId) {
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory record not found for product " + productId + " in warehouse " + warehouseId));
        return mapToDto(inventory);
    }

    @Override
    public List<InventoryDto> getInventoryByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private InventoryDto mapToDto(Inventory inventory) {
        InventoryDto dto = new InventoryDto();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProduct().getId());
        dto.setProductName(inventory.getProduct().getName());
        dto.setWarehouseId(inventory.getWarehouse().getId());
        dto.setWarehouseName(inventory.getWarehouse().getName());
        dto.setQuantity(inventory.getQuantity());
        dto.setReorderPoint(inventory.getReorderPoint());
        dto.setLastUpdated(inventory.getLastUpdated());
        return dto;
    }
}
