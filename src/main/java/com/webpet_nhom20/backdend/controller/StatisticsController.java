package com.webpet_nhom20.backdend.controller;

import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.statistics.*;
import com.webpet_nhom20.backdend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Admin Dashboard Statistics
 * Provides endpoints for various statistics including:
 * - Dashboard summary (total orders, revenue, customers)
 * - Order status counts
 * - Monthly revenue
 * - Top selling products
 * - Revenue by category and animal type
 * - Top customers
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Get dashboard summary statistics
     * Includes: total orders, total revenue, total customers, avg order value
     */
    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryDTO> getDashboardSummary() {
        log.info("API call: GET /api/admin/statistics/summary");

        DashboardSummaryDTO summary = statisticsService.getDashboardSummary();

        return ApiResponse.<DashboardSummaryDTO>builder()
                .success(true)
                .message("Dashboard summary retrieved successfully")
                .result(summary)
                .build();
    }

    /**
     * Get order count by status
     */
    @GetMapping("/orders/by-status")
    public ApiResponse<List<OrderStatusCountDTO>> getOrderCountByStatus() {
        log.info("API call: GET /api/admin/statistics/orders/by-status");

        List<OrderStatusCountDTO> statusCounts = statisticsService.getOrderCountByStatus();

        return ApiResponse.<List<OrderStatusCountDTO>>builder()
                .success(true)
                .message("Order status counts retrieved successfully")
                .result(statusCounts)
                .build();
    }

    /**
     * Get monthly revenue for the last N months
     * 
     * @param months Number of months (default: 12)
     */
    @GetMapping("/revenue/monthly")
    public ApiResponse<List<MonthlyRevenueDTO>> getMonthlyRevenue(
            @RequestParam(defaultValue = "12") int months) {
        log.info("API call: GET /api/admin/statistics/revenue/monthly?months={}", months);

        List<MonthlyRevenueDTO> monthlyRevenue = statisticsService.getMonthlyRevenue(months);

        return ApiResponse.<List<MonthlyRevenueDTO>>builder()
                .success(true)
                .message("Monthly revenue retrieved successfully")
                .result(monthlyRevenue)
                .build();
    }

    /**
     * Get top selling products
     * 
     * @param limit  Number of products to return (default: 5)
     * @param sortBy Sort by 'quantity' or 'revenue' (default: quantity)
     */
    @GetMapping("/products/top-selling")
    public ApiResponse<List<TopProductDTO>> getTopSellingProducts(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "quantity") String sortBy) {
        log.info("API call: GET /api/admin/statistics/products/top-selling?limit={}&sortBy={}", limit, sortBy);

        List<TopProductDTO> topProducts;
        if ("revenue".equalsIgnoreCase(sortBy)) {
            topProducts = statisticsService.getTopSellingProductsByRevenue(limit);
        } else {
            topProducts = statisticsService.getTopSellingProductsByQuantity(limit);
        }

        return ApiResponse.<List<TopProductDTO>>builder()
                .success(true)
                .message("Top selling products retrieved successfully")
                .result(topProducts)
                .build();
    }

    /**
     * Get revenue by product category
     */
    @GetMapping("/revenue/by-category")
    public ApiResponse<List<CategoryRevenueDTO>> getRevenueByCategory() {
        log.info("API call: GET /api/admin/statistics/revenue/by-category");

        List<CategoryRevenueDTO> categoryRevenue = statisticsService.getRevenueByCategory();

        return ApiResponse.<List<CategoryRevenueDTO>>builder()
                .success(true)
                .message("Revenue by category retrieved successfully")
                .result(categoryRevenue)
                .build();
    }

    /**
     * Get revenue by animal type (dog, cat, hamster, etc.)
     */
    @GetMapping("/revenue/by-animal-type")
    public ApiResponse<List<AnimalTypeRevenueDTO>> getRevenueByAnimalType() {
        log.info("API call: GET /api/admin/statistics/revenue/by-animal-type");

        List<AnimalTypeRevenueDTO> animalRevenue = statisticsService.getRevenueByAnimalType();

        return ApiResponse.<List<AnimalTypeRevenueDTO>>builder()
                .success(true)
                .message("Revenue by animal type retrieved successfully")
                .result(animalRevenue)
                .build();
    }

    /**
     * Get top customers by total spending
     * 
     * @param limit Number of customers to return (default: 5)
     */
    @GetMapping("/customers/top")
    public ApiResponse<List<TopCustomerDTO>> getTopCustomers(
            @RequestParam(defaultValue = "5") int limit) {
        log.info("API call: GET /api/admin/statistics/customers/top?limit={}", limit);

        List<TopCustomerDTO> topCustomers = statisticsService.getTopCustomers(limit);

        return ApiResponse.<List<TopCustomerDTO>>builder()
                .success(true)
                .message("Top customers retrieved successfully")
                .result(topCustomers)
                .build();
    }

    /**
     * Get daily revenue within a date range
     * 
     * @param startDate Start date (yyyy-MM-dd)
     * @param endDate   End date (yyyy-MM-dd)
     */
    @GetMapping("/revenue/daily")
    public ApiResponse<List<DailyRevenueDTO>> getDailyRevenue(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("API call: GET /api/admin/statistics/revenue/daily?startDate={}&endDate={}", startDate, endDate);

        List<DailyRevenueDTO> dailyRevenue = statisticsService.getDailyRevenue(startDate, endDate);

        return ApiResponse.<List<DailyRevenueDTO>>builder()
                .success(true)
                .message("Daily revenue retrieved successfully")
                .result(dailyRevenue)
                .build();
    }

    /**
     * Get weekly revenue within a date range
     * 
     * @param startDate Start date (yyyy-MM-dd)
     * @param endDate   End date (yyyy-MM-dd)
     */
    @GetMapping("/revenue/weekly")
    public ApiResponse<List<WeeklyRevenueDTO>> getWeeklyRevenue(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("API call: GET /api/admin/statistics/revenue/weekly?startDate={}&endDate={}", startDate, endDate);

        List<WeeklyRevenueDTO> weeklyRevenue = statisticsService.getWeeklyRevenue(startDate, endDate);

        return ApiResponse.<List<WeeklyRevenueDTO>>builder()
                .success(true)
                .message("Weekly revenue retrieved successfully")
                .result(weeklyRevenue)
                .build();
    }
}
