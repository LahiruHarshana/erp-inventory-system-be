package com.erp.inventory.system.repository;

import com.erp.inventory.system.model.OrderStatus;
import com.erp.inventory.system.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    @Query("SELECT po FROM PurchaseOrder po WHERE " +
            "(:startDate IS NULL OR po.orderDate >= :startDate) AND " +
            "(:endDate IS NULL OR po.orderDate <= :endDate) AND " +
            "(:supplierId IS NULL OR po.supplier.id = :supplierId) AND " +
            "(:status IS NULL OR po.status = :status)")
    List<PurchaseOrder> findByFilters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("supplierId") Long supplierId,
            @Param("status") OrderStatus status);


    @Query("SELECT pi.product.sku, SUM(pi.quantity) " +
            "FROM PurchaseOrder p JOIN p.orderItems pi " +
            "WHERE p.orderDate >= :startDate AND p.orderDate < :endDate " +
            "GROUP BY pi.product.sku")
    List<Object[]> findOrdersByProduct(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}