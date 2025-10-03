package com.erp.inventory.system.repository;

import com.erp.inventory.system.model.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {
}
