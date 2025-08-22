package com.erp.inventory.system.repository;

import com.erp.inventory.system.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
