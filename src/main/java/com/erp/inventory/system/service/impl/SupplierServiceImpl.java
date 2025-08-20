package com.erp.inventory.system.service.impl;

import com.erp.inventory.system.dto.SupplierDto;
import com.erp.inventory.system.model.Supplier;
import com.erp.inventory.system.repository.SupplierRepository;
import com.erp.inventory.system.service.SupplierService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public SupplierDto createSupplier(SupplierDto supplierDto) {
        supplierRepository.findByEmail(supplierDto.getEmail()).ifPresent(s -> {
            throw new IllegalStateException("Supplier with email '" + s.getEmail() + "' already exists.");
        });

        Supplier supplier = new Supplier();
        supplier.setName(supplierDto.getName());
        supplier.setContactPerson(supplierDto.getContactPerson());
        supplier.setEmail(supplierDto.getEmail());
        supplier.setPhone(supplierDto.getPhone());
        supplier.setApiUrl(supplierDto.getApiUrl());

        Supplier savedSupplier = supplierRepository.save(supplier);
        return mapToDto(savedSupplier);
    }

    @Override
    public SupplierDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));
        return mapToDto(supplier);
    }

    @Override
    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));

        existingSupplier.setName(supplierDto.getName());
        existingSupplier.setContactPerson(supplierDto.getContactPerson());
        existingSupplier.setEmail(supplierDto.getEmail());
        existingSupplier.setPhone(supplierDto.getPhone());
        existingSupplier.setApiUrl(supplierDto.getApiUrl());

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return mapToDto(updatedSupplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new EntityNotFoundException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }

    private SupplierDto mapToDto(Supplier supplier) {
        SupplierDto dto = new SupplierDto();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setEmail(supplier.getEmail());
        dto.setPhone(supplier.getPhone());
        dto.setApiUrl(supplier.getApiUrl());
        return dto;
    }
}
