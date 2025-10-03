package com.erp.inventory.system.repository;

import com.erp.inventory.system.model.Inventory;
import com.erp.inventory.system.model.Product;
import com.erp.inventory.system.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    List<Inventory> findByWarehouseId(Long warehouseId);
    List<Inventory> findByQuantityLessThanEqual(Integer quantity);
//    Inventory findByProductAndWarehouse(Product product, Warehouse warehouse);
    Inventory findByProductAndWarehouse(Product product, Warehouse warehouse);

    @Query("SELECT COUNT(DISTINCT i.product.id) FROM Inventory i WHERE i.quantity > 0")
    int countDistinctProducts();

    @Query("SELECT SUM(i.quantity) FROM Inventory i")
    Integer sumTotalQuantity();

    @Query("SELECT SUM(i.quantity * p.unitPrice) FROM Inventory i JOIN i.product p")
    Double sumTotalValue();

    @Query("SELECT i.warehouse.name, COUNT(DISTINCT i.product.id), SUM(i.quantity) " +
            "FROM Inventory i WHERE i.quantity > 0 GROUP BY i.warehouse.name")
    List<Object[]> findWarehouseSummaries();

    @Query("SELECT i FROM Inventory i JOIN i.product p JOIN p.supplier s " +
            "WHERE i.quantity <= :threshold")
    List<Inventory> findLowStockItems(@Param("threshold") int threshold);

    @Query("SELECT i.product.sku, SUM(i.quantity) " +
            "FROM Inventory i JOIN i.warehouse w JOIN SalesOrder s ON s.warehouse.id = w.id " +
            "WHERE s.buyerStore.storeRegistrationNumber = :storeRegistrationNumber " +
            "GROUP BY i.product.sku")
    List<Object[]> findInventoryByStore(@Param("storeRegistrationNumber") String storeRegistrationNumber);
}
