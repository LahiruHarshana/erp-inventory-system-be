package com.erp.inventory.system.repository;

import com.erp.inventory.system.model.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    @Query("SELECT s.buyerStore.storeRegistrationNumber, si.product.sku, SUM(si.quantity) " +
            "FROM SalesOrder s JOIN s.orderItems si " +
            "WHERE s.orderDate >= :startDate AND s.orderDate < :endDate " +
            "GROUP BY s.buyerStore.storeRegistrationNumber, si.product.sku")
    List<Object[]> findSalesByStoreAndProduct(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
