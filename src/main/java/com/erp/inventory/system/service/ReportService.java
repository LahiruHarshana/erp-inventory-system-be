package com.erp.inventory.system.service;
import com.erp.inventory.system.dto.InventorySummaryDto;
import com.erp.inventory.system.dto.LowStockItemDto;
import com.erp.inventory.system.dto.PurchaseOrderDto;
import com.erp.inventory.system.model.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    InventorySummaryDto getInventorySummary();
    List<PurchaseOrderDto> getPurchaseOrderHistory(LocalDate startDate, LocalDate endDate, Long supplierId, OrderStatus status);
    List<LowStockItemDto> getLowStockReport(Integer threshold);
}