package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.response.statistics.*;

import java.util.List;

/**
 * Service interface for statistics operations
 */
public interface StatisticsService {

    /**
     * Get dashboard summary including total orders, revenue, customers, and avg
     * order value
     */
    DashboardSummaryDTO getDashboardSummary();

    /**
     * Get order count grouped by status
     */
    List<OrderStatusCountDTO> getOrderCountByStatus();

    /**
     * Get monthly revenue for the last N months
     * 
     * @param months Number of months to include (default 12)
     */
    List<MonthlyRevenueDTO> getMonthlyRevenue(int months);

    /**
     * Get top selling products by quantity
     * 
     * @param limit Number of products to return (default 5)
     */
    List<TopProductDTO> getTopSellingProductsByQuantity(int limit);

    /**
     * Get top selling products by revenue
     * 
     * @param limit Number of products to return (default 5)
     */
    List<TopProductDTO> getTopSellingProductsByRevenue(int limit);

    /**
     * Get revenue grouped by product category
     */
    List<CategoryRevenueDTO> getRevenueByCategory();

    /**
     * Get revenue grouped by animal type
     */
    List<AnimalTypeRevenueDTO> getRevenueByAnimalType();

    /**
     * Get top customers by total spending
     * 
     * @param limit Number of customers to return (default 5)
     */
    List<TopCustomerDTO> getTopCustomers(int limit);

    /**
     * Get daily revenue within a date range
     * 
     * @param startDate Start date (yyyy-MM-dd)
     * @param endDate   End date (yyyy-MM-dd)
     */
    List<DailyRevenueDTO> getDailyRevenue(String startDate, String endDate);

    /**
     * Get weekly revenue within a date range
     * 
     * @param startDate Start date (yyyy-MM-dd)
     * @param endDate   End date (yyyy-MM-dd)
     */
    List<WeeklyRevenueDTO> getWeeklyRevenue(String startDate, String endDate);
}
