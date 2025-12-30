package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.dto.response.statistics.*;
import com.webpet_nhom20.backdend.repository.StatisticsRepository;
import com.webpet_nhom20.backdend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of StatisticsService
 * Handles all statistics calculations for the admin dashboard
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticsRepository;

    @Override
    public DashboardSummaryDTO getDashboardSummary() {
        log.info("Fetching dashboard summary statistics");

        Long totalOrders = statisticsRepository.countTotalOrders();
        Long totalCustomers = statisticsRepository.countTotalCustomers();
        BigDecimal totalRevenue = statisticsRepository.calculateTotalRevenue();
        Long paidOrderCount = statisticsRepository.countPaidOrders();

        // Calculate average order value
        BigDecimal avgOrderValue = BigDecimal.ZERO;
        if (paidOrderCount != null && paidOrderCount > 0 && totalRevenue != null) {
            avgOrderValue = totalRevenue.divide(
                    BigDecimal.valueOf(paidOrderCount),
                    2,
                    RoundingMode.HALF_UP);
        }

        return DashboardSummaryDTO.builder()
                .totalOrders(totalOrders != null ? totalOrders : 0L)
                .totalCustomers(totalCustomers != null ? totalCustomers : 0L)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .paidOrderCount(paidOrderCount != null ? paidOrderCount : 0L)
                .avgOrderValue(avgOrderValue)
                .build();
    }

    @Override
    public List<OrderStatusCountDTO> getOrderCountByStatus() {
        log.info("Fetching order count by status");

        List<Object[]> results = statisticsRepository.countOrdersByStatus();
        List<OrderStatusCountDTO> statusCounts = new ArrayList<>();

        for (Object[] row : results) {
            statusCounts.add(OrderStatusCountDTO.builder()
                    .status((String) row[0])
                    .count(((Number) row[1]).longValue())
                    .build());
        }

        return statusCounts;
    }

    @Override
    public List<MonthlyRevenueDTO> getMonthlyRevenue(int months) {
        log.info("Fetching monthly revenue for last {} months", months);

        List<Object[]> results = statisticsRepository.getMonthlyRevenue(months);
        List<MonthlyRevenueDTO> monthlyRevenues = new ArrayList<>();

        for (Object[] row : results) {
            Integer year = ((Number) row[0]).intValue();
            Integer month = ((Number) row[1]).intValue();

            monthlyRevenues.add(MonthlyRevenueDTO.builder()
                    .year(year)
                    .month(month)
                    .monthLabel(String.format("%d-%02d", year, month))
                    .revenue(row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO)
                    .orderCount(((Number) row[3]).longValue())
                    .build());
        }

        return monthlyRevenues;
    }

    @Override
    public List<TopProductDTO> getTopSellingProductsByQuantity(int limit) {
        log.info("Fetching top {} selling products by quantity", limit);
        return mapToTopProductDTO(statisticsRepository.getTopSellingProductsByQuantity(limit));
    }

    @Override
    public List<TopProductDTO> getTopSellingProductsByRevenue(int limit) {
        log.info("Fetching top {} selling products by revenue", limit);
        return mapToTopProductDTO(statisticsRepository.getTopSellingProductsByRevenue(limit));
    }

    private List<TopProductDTO> mapToTopProductDTO(List<Object[]> results) {
        List<TopProductDTO> products = new ArrayList<>();

        for (Object[] row : results) {
            products.add(TopProductDTO.builder()
                    .productId(((Number) row[0]).intValue())
                    .productName((String) row[1])
                    .categoryName((String) row[2])
                    .animalType((String) row[3])
                    .totalQuantitySold(((Number) row[4]).longValue())
                    .totalRevenue(row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO)
                    .build());
        }

        return products;
    }

    @Override
    public List<CategoryRevenueDTO> getRevenueByCategory() {
        log.info("Fetching revenue by category");

        List<Object[]> results = statisticsRepository.getRevenueByCategory();
        List<CategoryRevenueDTO> categoryRevenues = new ArrayList<>();

        for (Object[] row : results) {
            categoryRevenues.add(CategoryRevenueDTO.builder()
                    .categoryId(row[0] != null ? ((Number) row[0]).intValue() : null)
                    .categoryName(row[1] != null ? (String) row[1] : "Không có danh mục")
                    .totalRevenue(row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO)
                    .orderCount(((Number) row[3]).longValue())
                    .totalQuantitySold(((Number) row[4]).longValue())
                    .build());
        }

        return categoryRevenues;
    }

    @Override
    public List<AnimalTypeRevenueDTO> getRevenueByAnimalType() {
        log.info("Fetching revenue by animal type");

        List<Object[]> results = statisticsRepository.getRevenueByAnimalType();
        List<AnimalTypeRevenueDTO> animalRevenues = new ArrayList<>();

        for (Object[] row : results) {
            animalRevenues.add(AnimalTypeRevenueDTO.builder()
                    .animalType((String) row[0])
                    .totalRevenue(row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO)
                    .orderCount(((Number) row[2]).longValue())
                    .totalQuantitySold(((Number) row[3]).longValue())
                    .build());
        }

        return animalRevenues;
    }

    @Override
    public List<TopCustomerDTO> getTopCustomers(int limit) {
        log.info("Fetching top {} customers by spending", limit);

        List<Object[]> results = statisticsRepository.getTopCustomers(limit);
        List<TopCustomerDTO> topCustomers = new ArrayList<>();

        for (Object[] row : results) {
            topCustomers.add(TopCustomerDTO.builder()
                    .userId(((Number) row[0]).intValue())
                    .customerName((String) row[1])
                    .email((String) row[2])
                    .phone((String) row[3])
                    .totalSpent(row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO)
                    .orderCount(((Number) row[5]).longValue())
                    .build());
        }

        return topCustomers;
    }

    @Override
    public List<DailyRevenueDTO> getDailyRevenue(String startDate, String endDate) {
        log.info("Fetching daily revenue from {} to {}", startDate, endDate);

        List<Object[]> results = statisticsRepository.getDailyRevenue(startDate, endDate);
        List<DailyRevenueDTO> dailyRevenues = new ArrayList<>();

        for (Object[] row : results) {
            java.sql.Date sqlDate = (java.sql.Date) row[0];
            java.time.LocalDate localDate = sqlDate.toLocalDate();

            dailyRevenues.add(DailyRevenueDTO.builder()
                    .date(localDate)
                    .dateLabel(localDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .revenue(row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO)
                    .orderCount(((Number) row[2]).longValue())
                    .build());
        }

        return dailyRevenues;
    }

    @Override
    public List<WeeklyRevenueDTO> getWeeklyRevenue(String startDate, String endDate) {
        log.info("Fetching weekly revenue from {} to {}", startDate, endDate);

        List<Object[]> results = statisticsRepository.getWeeklyRevenue(startDate, endDate);
        List<WeeklyRevenueDTO> weeklyRevenues = new ArrayList<>();

        for (Object[] row : results) {
            Integer year = ((Number) row[0]).intValue();
            Integer weekNumber = ((Number) row[1]).intValue();

            weeklyRevenues.add(WeeklyRevenueDTO.builder()
                    .year(year)
                    .weekNumber(weekNumber)
                    .weekLabel(String.format("Tuần %d/%d", weekNumber, year))
                    .revenue(row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO)
                    .orderCount(((Number) row[3]).longValue())
                    .build());
        }

        return weeklyRevenues;
    }
}
