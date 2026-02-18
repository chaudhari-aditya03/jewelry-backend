package com.jewelryshop.service;

import com.jewelryshop.dto.DashboardSummaryResponse;
import com.jewelryshop.dto.MonthlySalesResponse;

import java.util.List;

public interface AnalyticsService {
    DashboardSummaryResponse getDashboardSummary();
    List<MonthlySalesResponse> getMonthlySales(int year);
}
