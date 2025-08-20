package com.erp.inventory.system.controller;

import com.erp.inventory.system.dto.StoreDto;
import com.erp.inventory.system.service.StoreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Tag(name = "2. Store Management", description = "APIs for managing stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_BUSINESS_OWNER')")
    public ResponseEntity<StoreDto> createStore(@RequestBody StoreDto storeDto) {
        StoreDto createdStore = storeService.createStore(storeDto);
        return new ResponseEntity<>(createdStore, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_BUSINESS_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStoreById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_BUSINESS_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<List<StoreDto>> getAllStores() {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_BUSINESS_OWNER')")
    public ResponseEntity<StoreDto> updateStore(@PathVariable Long id, @RequestBody StoreDto storeDto) {
        return ResponseEntity.ok(storeService.updateStore(id, storeDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_BUSINESS_OWNER')")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }
}