package com.erp.inventory.system.service;

import com.erp.inventory.system.dto.SalesRecordDto;

import java.time.LocalDate;
import java.util.List;

public interface SalesRecordService {
    List<SalesRecordDto> getSalesRecords(LocalDate startDate, LocalDate endDate);
}