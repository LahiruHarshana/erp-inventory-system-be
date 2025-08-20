package com.erp.inventory.system.repository;

import com.erp.inventory.system.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    List<Inventory> findByWarehouseId(Long warehouseId);
    List<Inventory> findByQuantityLessThanEqual(Integer quantity);
}
