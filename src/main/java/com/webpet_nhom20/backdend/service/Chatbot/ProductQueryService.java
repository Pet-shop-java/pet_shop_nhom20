package com.webpet_nhom20.backdend.service.Chatbot;

import com.webpet_nhom20.backdend.entity.Categories;
import com.webpet_nhom20.backdend.entity.Products;
import com.webpet_nhom20.backdend.entity.ServicesPet;
import com.webpet_nhom20.backdend.repository.CategoryRepository;
import com.webpet_nhom20.backdend.repository.ProductRepository;
import com.webpet_nhom20.backdend.repository.ServicesPetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Service để query sản phẩm VÀ dịch vụ từ DB (với caching)
 */
@Service
public class ProductQueryService {

    private final ProductRepository productRepository;
    private final ServicesPetRepository servicesPetRepository;
    private final CategoryRepository categoryRepository;

    // Cache
    private static final Duration CACHE_TTL = Duration.ofMinutes(10); // Cache 10 phút
    private List<Products> cachedProducts = null;
    private List<ServicesPet> cachedServices = null;
    private Instant productsLastFetch = null;
    private Instant servicesLastFetch = null;

    public ProductQueryService(
            ProductRepository productRepository,
            ServicesPetRepository servicesPetRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.servicesPetRepository = servicesPetRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Search sản phẩm từ DB dựa trên question (với cache)
     *
     * @param question câu hỏi của user
     * @param limit    số lượng sản phẩm tối đa
     * @return danh sách sản phẩm
     */
    @Transactional(readOnly = true)
    public List<Products> searchProducts(String question, int limit) {
        if (question == null || question.isBlank()) {
            return List.of();
        }

        // Check cache
        if (cachedProducts != null && productsLastFetch != null) {
            Duration age = Duration.between(productsLastFetch, Instant.now());
            if (age.compareTo(CACHE_TTL) < 0) {
                System.out.println(
                        "✅ Using CACHED products (" + cachedProducts.size() + " items, age: " + age.toSeconds() + "s)");
                return cachedProducts.subList(0, Math.min(limit, cachedProducts.size()));
            }
        }

        System.out.println("🔍 ProductQueryService: Querying ALL products from DB (limit=" + limit + ")");

        // Query TẤT CẢ sản phẩm active, để Gemini tự lọc
        Pageable pageable = PageRequest.of(0, limit);
        Page<Products> page = productRepository.findAllWithFilters(
                null, // categoryId = null (query all categories)
                null, // animal = null (query all animals)
                null, // brand = null
                "0", // is_deleted = 0 (chỉ lấy active)
                null, // is_featured
                null, // nameKeyword = null (không filter tên)
                null, // minPrice
                null, // maxPrice
                pageable);

        List<Products> results = page.getContent();

        System.out.println("✅ Found " + results.size() + " products");

        // Force initialize lazy collections để tránh LazyInitializationException
        for (Products p : results) {
            try {
                // Touch collections to initialize them
                if (p.getProduct_variants() != null) {
                    p.getProduct_variants().size();
                }
                if (p.getCategory() != null) {
                    p.getCategory().getName();
                }
            } catch (Exception e) {
                System.out.println("⚠️ Cannot initialize product: " + p.getId());
            }
        }

        // Save to cache
        cachedProducts = results;
        productsLastFetch = Instant.now();
        System.out.println("💾 Cached " + results.size() + " products");

        return results;
    }

    /**
     * Search dịch vụ từ DB (với cache)
     *
     * @param limit số lượng dịch vụ tối đa
     * @return danh sách dịch vụ
     */
    @Transactional(readOnly = true)
    public List<ServicesPet> searchServices(int limit) {
        // Check cache
        if (cachedServices != null && servicesLastFetch != null) {
            Duration age = Duration.between(servicesLastFetch, Instant.now());
            if (age.compareTo(CACHE_TTL) < 0) {
                System.out.println(
                        "✅ Using CACHED services (" + cachedServices.size() + " items, age: " + age.toSeconds() + "s)");
                return cachedServices.subList(0, Math.min(limit, cachedServices.size()));
            }
        }

        System.out.println("🔍 ProductQueryService: Querying services from DB (limit=" + limit + ")");

        Pageable pageable = PageRequest.of(0, limit);
        Page<ServicesPet> page = servicesPetRepository.findAllOrderByActiveAndCreated(pageable);

        List<ServicesPet> results = page.getContent();
        System.out.println("✅ Found " + results.size() + " services");

        // Save to cache
        cachedServices = results;
        servicesLastFetch = Instant.now();
        System.out.println("💾 Cached " + results.size() + " services");

        return results;
    }

    // Không cần các method extract nữa, comment/xóa được

    /**
     * Tìm category ID theo tên (case-insensitive)
     * Giữ lại để dùng trong tương lai nếu cần
     */
    private Integer findCategoryIdByName(String name) {
        try {
            Pageable pageable = PageRequest.of(0, 1);
            Page<Categories> page = categoryRepository.findByNameContainingIgnoreCase(name, pageable);
            if (!page.isEmpty()) {
                return page.getContent().get(0).getId();
            }
        } catch (Exception e) {
            // Log nếu cần
        }
        return null;
    }

    /**
     * Extract keyword cho product name
     * Loại bỏ stopwords, giữ keywords chính
     */
    private String extractNameKeyword(String question) {
        // Remove common words
        String cleaned = question
                .replaceAll("\\b(tôi|mình|bạn|có|là|gì|cho|và|của|với|trong|được)\\b", " ")
                .replaceAll("\\s+", " ")
                .trim();

        // Nếu quá ngắn, return null
        if (cleaned.length() < 3) {
            return null;
        }

        return cleaned;
    }
}