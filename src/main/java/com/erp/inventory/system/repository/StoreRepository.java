package com.erp.inventory.system.repository;

import com.erp.inventory.system.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByStoreRegistrationNumber(String storeRegistrationNumber);
}
