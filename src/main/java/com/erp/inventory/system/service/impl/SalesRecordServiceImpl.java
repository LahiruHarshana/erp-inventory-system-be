package com.erp.inventory.system.service.impl;

import com.erp.inventory.system.dto.SalesRecordDto;
import com.erp.inventory.system.model.Product;
import com.erp.inventory.system.model.Store;
import com.erp.inventory.system.repository.*;
import com.erp.inventory.system.service.SalesRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SalesRecordServiceImpl implements SalesRecordService {

    private final SalesOrderRepository salesOrderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryRepository inventoryRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SalesRecordDto> getSalesRecords(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = startDate.plusDays(1); // Default to single day
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }

        // Convert LocalDate to LocalDateTime for queries
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay();

        // Fetch sales data
        List<Object[]> salesData = salesOrderRepository.findSalesByStoreAndProduct(startDateTime, endDateTime);
        Map<String, Map<String, Integer>> salesMap = new HashMap<>();
        for (Object[] row : salesData) {
            String storeId = (String) row[0];
            String productId = (String) row[1];
            Long unitsSold = (Long) row[2];
            salesMap.computeIfAbsent(storeId, k -> new HashMap<>()).put(productId, unitsSold.intValue());
        }

        // Fetch purchase order data
        List<Object[]> orderData = purchaseOrderRepository.findOrdersByProduct(startDateTime, endDateTime);
        Map<String, Integer> orderMap = new HashMap<>();
        for (Object[] row : orderData) {
            String productId = (String) row[0];
            Long unitsOrdered = (Long) row[1];
            orderMap.put(productId, unitsOrdered.intValue());
        }

        // Build records
        List<SalesRecordDto> records = new ArrayList<>();
        List<Store> stores = storeRepository.findAll();

        for (Store store : stores) {
            String storeId = store.getStoreRegistrationNumber();
            List<Object[]> inventoryData = inventoryRepository.findInventoryByStore(storeId);

            for (Object[] inventory : inventoryData) {
                System.out.println(Arrays.toString(inventory));
                String productId = (String) inventory[0];
                Integer inventoryLevel = ((Long) inventory[1]).intValue();

                Optional<Product> product = productRepository.findBySku(productId);
                if (product.isEmpty()) {
                    continue;
                }

                SalesRecordDto dto = new SalesRecordDto();
                dto.setDate(startDate);
                dto.setStoreId(storeId);
                dto.setProductId(productId);
                dto.setCategory(product.get().getCategory() != null ? product.get().getCategory().getName() : "Unknown");
                dto.setInventoryLevel(inventoryLevel);
                dto.setUnitsSold(salesMap.getOrDefault(storeId, new HashMap<>()).getOrDefault(productId, 0));
                dto.setUnitsOrdered(orderMap.getOrDefault(productId, 0));
                dto.setPrice(BigDecimal.valueOf(product.get().getUnitPrice()));

                // Mock data to match provided example
                dto.setRegion("North");
                dto.setDiscount(BigDecimal.valueOf(0.1));
                dto.setWeatherCondition("Sunny");
                dto.setHolidayPromotion(0);
                dto.setCompetitorPricing(BigDecimal.valueOf(145.00));
                dto.setSeasonality("Summer");

                records.add(dto);
            }
        }

        return records;
    }
}