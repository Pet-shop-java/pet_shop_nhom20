package com.webpet_nhom20.backdend.repository;

import com.webpet_nhom20.backdend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for statistics queries
 * Uses native SQL for complex aggregations and better performance
 */
@Repository
public interface StatisticsRepository extends JpaRepository<Order, Integer> {

    // ======================== DASHBOARD SUMMARY ========================

    /**
     * Count total orders (excluding deleted)
     */
    @Query(value = "SELECT COUNT(*) FROM orders WHERE is_deleted = '0'", nativeQuery = true)
    Long countTotalOrders();

    /**
     * Count total customers (users with at least one order)
     */
    @Query(value = """
            SELECT COUNT(DISTINCT user_id)
            FROM orders
            WHERE is_deleted = '0'
            """, nativeQuery = true)
    Long countTotalCustomers();

    /**
     * Calculate total revenue from COMPLETED and DELIVERED orders
     */
    @Query(value = """
            SELECT COALESCE(SUM(final_amount), 0)
            FROM orders
            WHERE status IN ('COMPLETED', 'DELIVERED')
            AND is_deleted = '0'
            """, nativeQuery = true)
    BigDecimal calculateTotalRevenue();

    /**
     * Count paid orders (COMPLETED or DELIVERED)
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM orders
            WHERE status IN ('COMPLETED', 'DELIVERED')
            AND is_deleted = '0'
            """, nativeQuery = true)
    Long countPaidOrders();

    // ======================== ORDER STATUS COUNT ========================

    /**
     * Count orders grouped by status
     * Returns: [status, count]
     */
    @Query(value = """
            SELECT status, COUNT(*) as count
            FROM orders
            WHERE is_deleted = '0'
            GROUP BY status
            ORDER BY count DESC
            """, nativeQuery = true)
    List<Object[]> countOrdersByStatus();

    // ======================== MONTHLY REVENUE ========================

    /**
     * Get revenue by month for the last N months
     * Returns: [year, month, revenue, order_count]
     */
    @Query(value = """
            SELECT
                YEAR(created_date) as year,
                MONTH(created_date) as month,
                COALESCE(SUM(final_amount), 0) as revenue,
                COUNT(*) as order_count
            FROM orders
            WHERE status IN ('COMPLETED', 'DELIVERED')
            AND is_deleted = '0'
            AND created_date >= DATE_SUB(CURDATE(), INTERVAL :months MONTH)
            GROUP BY YEAR(created_date), MONTH(created_date)
            ORDER BY year DESC, month DESC
            """, nativeQuery = true)
    List<Object[]> getMonthlyRevenue(@Param("months") int months);

    // ======================== TOP SELLING PRODUCTS ========================

    /**
     * Get top selling products by quantity
     * Returns: [product_id, product_name, category_name, animal_type,
     * total_quantity, total_revenue]
     */
    @Query(value = """
            SELECT
                p.id as product_id,
                p.name as product_name,
                c.name as category_name,
                p.animal as animal_type,
                SUM(oi.quantity) as total_quantity,
                SUM(oi.total_price) as total_revenue
            FROM order_items oi
            JOIN orders o ON oi.order_id = o.id
            JOIN product_variants pv ON oi.product_variant_id = pv.id
            JOIN products p ON pv.product_id = p.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE o.status IN ('COMPLETED', 'DELIVERED')
            AND o.is_deleted = '0'
            AND oi.is_deleted = '0'
            GROUP BY p.id, p.name, c.name, p.animal
            ORDER BY total_quantity DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> getTopSellingProductsByQuantity(@Param("limit") int limit);

    /**
     * Get top selling products by revenue
     * Returns: [product_id, product_name, category_name, animal_type,
     * total_quantity, total_revenue]
     */
    @Query(value = """
            SELECT
                p.id as product_id,
                p.name as product_name,
                c.name as category_name,
                p.animal as animal_type,
                SUM(oi.quantity) as total_quantity,
                SUM(oi.total_price) as total_revenue
            FROM order_items oi
            JOIN orders o ON oi.order_id = o.id
            JOIN product_variants pv ON oi.product_variant_id = pv.id
            JOIN products p ON pv.product_id = p.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE o.status IN ('COMPLETED', 'DELIVERED')
            AND o.is_deleted = '0'
            AND oi.is_deleted = '0'
            GROUP BY p.id, p.name, c.name, p.animal
            ORDER BY total_revenue DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> getTopSellingProductsByRevenue(@Param("limit") int limit);

    // ======================== REVENUE BY CATEGORY ========================

    /**
     * Get revenue grouped by category
     * Returns: [category_id, category_name, total_revenue, order_count,
     * total_quantity]
     */
    @Query(value = """
            SELECT
                c.id as category_id,
                c.name as category_name,
                SUM(oi.total_price) as total_revenue,
                COUNT(DISTINCT o.id) as order_count,
                SUM(oi.quantity) as total_quantity
            FROM order_items oi
            JOIN orders o ON oi.order_id = o.id
            JOIN product_variants pv ON oi.product_variant_id = pv.id
            JOIN products p ON pv.product_id = p.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE o.status IN ('COMPLETED', 'DELIVERED')
            AND o.is_deleted = '0'
            AND oi.is_deleted = '0'
            GROUP BY c.id, c.name
            ORDER BY total_revenue DESC
            """, nativeQuery = true)
    List<Object[]> getRevenueByCategory();

    // ======================== REVENUE BY ANIMAL TYPE ========================

    /**
     * Get revenue grouped by animal type
     * Returns: [animal_type, total_revenue, order_count, total_quantity]
     */
    @Query(value = """
            SELECT
                COALESCE(p.animal, 'Khác') as animal_type,
                SUM(oi.total_price) as total_revenue,
                COUNT(DISTINCT o.id) as order_count,
                SUM(oi.quantity) as total_quantity
            FROM order_items oi
            JOIN orders o ON oi.order_id = o.id
            JOIN product_variants pv ON oi.product_variant_id = pv.id
            JOIN products p ON pv.product_id = p.id
            WHERE o.status IN ('COMPLETED', 'DELIVERED')
            AND o.is_deleted = '0'
            AND oi.is_deleted = '0'
            GROUP BY p.animal
            ORDER BY total_revenue DESC
            """, nativeQuery = true)
    List<Object[]> getRevenueByAnimalType();

    // ======================== TOP CUSTOMERS ========================

    /**
     * Get top customers by total spending
     * Returns: [user_id, full_name, email, phone, total_spent, order_count]
     */
    @Query(value = """
            SELECT
                u.id as user_id,
                u.full_name as full_name,
                u.email as email,
                u.phone as phone,
                SUM(o.final_amount) as total_spent,
                COUNT(o.id) as order_count
            FROM orders o
            JOIN users u ON o.user_id = u.id
            WHERE o.status IN ('COMPLETED', 'DELIVERED')
            AND o.is_deleted = '0'
            GROUP BY u.id, u.full_name, u.email, u.phone
            ORDER BY total_spent DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> getTopCustomers(@Param("limit") int limit);
}
