package com.erp.inventory.system.service;

import com.erp.inventory.system.dto.StoreDto;

import java.util.List;

public interface StoreService {
    StoreDto createStore(StoreDto storeDto);
    StoreDto getStoreById(Long id);
    List<StoreDto> getAllStores();
    StoreDto updateStore(Long id, StoreDto storeDto);
    void deleteStore(Long id);

}