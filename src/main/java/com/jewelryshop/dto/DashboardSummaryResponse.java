package com.jewelryshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private Long totalUsers;
    private Long totalOrders;
    private Long activeOrders;
    private BigDecimal totalRevenue;
    private Long totalProducts;
    private Long lowStockProducts;
}
