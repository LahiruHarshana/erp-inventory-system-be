package com.erp.inventory.system.repository;

import com.erp.inventory.system.model.Store;
import com.erp.inventory.system.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
