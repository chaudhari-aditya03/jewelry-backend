package com.jewelryshop.controller;

import com.jewelryshop.dto.ApiResponse;
import com.jewelryshop.dto.DashboardSummaryResponse;
import com.jewelryshop.dto.MonthlySalesResponse;
import com.jewelryshop.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and reporting APIs")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary (Admin)")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary() {
        DashboardSummaryResponse summary = analyticsService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/monthly")
    @Operation(summary = "Get monthly sales report (Admin)")
    public ResponseEntity<ApiResponse<List<MonthlySalesResponse>>> getMonthlySales(
            @RequestParam(defaultValue = "2026") int year) {
        List<MonthlySalesResponse> monthlySales = analyticsService.getMonthlySales(year);
        return ResponseEntity.ok(ApiResponse.success(monthlySales));
    }
}
