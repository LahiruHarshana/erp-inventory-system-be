package com.erp.inventory.system.service.impl;

import com.erp.inventory.system.dto.StoreDto;
import com.erp.inventory.system.model.Store;
import com.erp.inventory.system.repository.StoreRepository;
import com.erp.inventory.system.service.StoreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Override
    public StoreDto createStore(StoreDto storeDto) {
        storeRepository.findByStoreRegistrationNumber(storeDto.getStoreRegistrationNumber())
                .ifPresent(s -> {
                    throw new IllegalStateException("Store with registration number " + s.getStoreRegistrationNumber() + " already exists.");
                });

        Store store = new Store();
        store.setStoreName(storeDto.getStoreName());
        store.setStoreRegistrationNumber(storeDto.getStoreRegistrationNumber());
        store.setTaxId(storeDto.getTaxId());
        store.setOperationalDetails(storeDto.getOperationalDetails());
        store.setVerified(true);

        Store savedStore = storeRepository.save(store);

        return mapToDto(savedStore);
    }

    @Override
    public StoreDto getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + id));
        return mapToDto(store);
    }

    @Override
    public List<StoreDto> getAllStores() {
        return storeRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public StoreDto updateStore(Long id, StoreDto storeDto) {
        Store existingStore = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + id));

        storeRepository.findByStoreRegistrationNumber(storeDto.getStoreRegistrationNumber())
                .ifPresent(s -> {
                    if (!s.getId().equals(id)) {
                        throw new IllegalStateException("Store with registration number " + s.getStoreRegistrationNumber() + " already exists.");
                    }
                });

        existingStore.setStoreName(storeDto.getStoreName());
        existingStore.setStoreRegistrationNumber(storeDto.getStoreRegistrationNumber());
        existingStore.setTaxId(storeDto.getTaxId());
        existingStore.setOperationalDetails(storeDto.getOperationalDetails());
        existingStore.setVerified(storeDto.isVerified());
        // Note: Verification status is not updated here. It should be a separate admin process.

        Store updatedStore = storeRepository.save(existingStore);
        return mapToDto(updatedStore);
    }

    @Override
    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new EntityNotFoundException("Store not found with id: " + id);
        }
        storeRepository.deleteById(id);
    }

    private StoreDto mapToDto(Store store) {
        StoreDto dto = new StoreDto();
        dto.setId(store.getId());
        dto.setStoreName(store.getStoreName());
        dto.setStoreRegistrationNumber(store.getStoreRegistrationNumber());
        dto.setTaxId(store.getTaxId());
        dto.setOperationalDetails(store.getOperationalDetails());
        dto.setVerified(store.isVerified());
        return dto;
    }
}
