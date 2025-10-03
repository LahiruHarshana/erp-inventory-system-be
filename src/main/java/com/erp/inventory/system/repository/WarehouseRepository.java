package com.erp.inventory.system.repository;

import com.erp.inventory.system.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
//    List<Warehouse> findByStoreId(Long storeId);
}