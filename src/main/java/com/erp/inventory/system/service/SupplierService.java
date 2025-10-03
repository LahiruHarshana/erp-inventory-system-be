package com.erp.inventory.system.service;

import com.erp.inventory.system.dto.SupplierDto;

import java.util.List;

public interface SupplierService {
    SupplierDto createSupplier(SupplierDto supplierDto);
    SupplierDto getSupplierById(Long id);
    List<SupplierDto> getAllSuppliers();
    SupplierDto updateSupplier(Long id, SupplierDto supplierDto);
    void deleteSupplier(Long id);
}