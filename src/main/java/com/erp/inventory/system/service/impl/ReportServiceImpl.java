package com.erp.inventory.system.service.impl;

import com.erp.inventory.system.dto.InventorySummaryDto;
import com.erp.inventory.system.dto.LowStockItemDto;
import com.erp.inventory.system.dto.PurchaseOrderDto;
import com.erp.inventory.system.dto.PurchaseOrderItemDto;
import com.erp.inventory.system.model.Inventory;
import com.erp.inventory.system.model.OrderStatus;
import com.erp.inventory.system.model.PurchaseOrder;
import com.erp.inventory.system.model.PurchaseOrderItem;
import com.erp.inventory.system.repository.InventoryRepository;
import com.erp.inventory.system.repository.PurchaseOrderRepository;
import com.erp.inventory.system.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final InventoryRepository inventoryRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public InventorySummaryDto getInventorySummary() {
        InventorySummaryDto summary = new InventorySummaryDto();
        summary.setTotalProducts(inventoryRepository.countDistinctProducts());
        summary.setTotalStockQuantity(inventoryRepository.sumTotalQuantity() != null ? inventoryRepository.sumTotalQuantity() : 0);
        summary.setTotalStockValue(BigDecimal.valueOf(inventoryRepository.sumTotalValue() != null ? inventoryRepository.sumTotalValue() : 0.0));

        List<InventorySummaryDto.WarehouseSummaryDto> warehouseSummaries = inventoryRepository.findWarehouseSummaries()
                .stream()
                .map(result -> {
                    InventorySummaryDto.WarehouseSummaryDto warehouseDto = new InventorySummaryDto.WarehouseSummaryDto();
                    warehouseDto.setWarehouseName((String) result[0]);
                    warehouseDto.setProductCount(((Long) result[1]).intValue());
                    warehouseDto.setTotalQuantity(((Long) result[2]).intValue());
                    return warehouseDto;
                })
                .collect(Collectors.toList());

        summary.setProductsByWarehouse(warehouseSummaries);
        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getPurchaseOrderHistory(LocalDate startDate, LocalDate endDate, Long supplierId, OrderStatus status) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        List<PurchaseOrder> orders = purchaseOrderRepository.findByFilters(startDateTime, endDateTime, supplierId, status);
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LowStockItemDto> getLowStockReport(Integer threshold) {
        int effectiveThreshold = threshold != null ? threshold : 10; // Default threshold
        List<Inventory> lowStockItems = inventoryRepository.findLowStockItems(effectiveThreshold);
        return lowStockItems.stream()
                .map(this::mapToLowStockItemDto)
                .collect(Collectors.toList());
    }

    private PurchaseOrderDto mapToDto(PurchaseOrder order) {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(order.getId());
        dto.setSupplierId(order.getSupplier().getId());
        dto.setSupplierName(order.getSupplier().getName());
        dto.setOrderDate(order.getOrderDate());
        dto.setExpectedDeliveryDate(order.getExpectedDeliveryDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::mapItemToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private PurchaseOrderItemDto mapItemToDto(PurchaseOrderItem item) {
        PurchaseOrderItemDto dto = new PurchaseOrderItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        return dto;
    }

    private LowStockItemDto mapToLowStockItemDto(Inventory inventory) {
        LowStockItemDto dto = new LowStockItemDto();
        dto.setProductId(inventory.getProduct().getId());
        dto.setProductName(inventory.getProduct().getName());
        dto.setSku(inventory.getProduct().getSku());
        dto.setWarehouseName(inventory.getWarehouse().getName());
        dto.setCurrentQuantity(inventory.getQuantity());
        dto.setSupplierName(inventory.getProduct().getSupplier() != null ? inventory.getProduct().getSupplier().getName() : "N/A");
        return dto;
    }
}