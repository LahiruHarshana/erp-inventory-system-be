package com.erp.inventory.system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DemoController {

    @GetMapping("/inventory/data")
    @PreAuthorize("hasAnyAuthority('ROLE_INVENTORY_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<String> getInventoryData() {
        return ResponseEntity.ok("Access Granted: This is sensitive inventory data.");
    }

    @GetMapping("/supply-chain/logistics")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPPLY_CHAIN_COORDINATOR', 'ROLE_ADMIN')")
    public ResponseEntity<String> getLogistics() {
        return ResponseEntity.ok("Access Granted: This is supply chain logistics information.");
    }

    @GetMapping("/business/overview")
    @PreAuthorize("hasAuthority('ROLE_BUSINESS_OWNER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> getBusinessOverview() {
        return ResponseEntity.ok("Access Granted: Here is the high-level business overview.");
    }

    @GetMapping("/admin/panel")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> getAdminPanel() {
        return ResponseEntity.ok("Welcome to the Admin Panel.");
    }
}