package com.webpet_nhom20.backdend.service.Chatbot;

import com.webpet_nhom20.backdend.entity.Pets;
import com.webpet_nhom20.backdend.entity.ProductVariants;
import com.webpet_nhom20.backdend.entity.Products;
import com.webpet_nhom20.backdend.entity.ServicesPet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * Build context string từ danh sách Products, Services VÀ Pets (DB entities)
 */
@Service
public class DBContextBuilder {

    private static final int MAX_DESCRIPTION_LEN = 200;

    /**
     * Build context từ CẢ sản phẩm, dịch vụ VÀ thú cưng
     */
    public String buildCombined(List<Products> products, List<ServicesPet> services, List<Pets> pets) {
        StringBuilder ctx = new StringBuilder();

        // 1. Products section
        if (products != null && !products.isEmpty()) {
            ctx.append("=== SẢN PHẨM ===\n\n");
            ctx.append(build(products));
        }

        // 2. Services section
        if (services != null && !services.isEmpty()) {
            if (ctx.length() > 0) {
                ctx.append("\n\n");
            }
            ctx.append("=== DỊCH VỤ ===\n\n");
            ctx.append(buildServices(services));
        }

        // 3. Pets section
        if (pets != null && !pets.isEmpty()) {
            if (ctx.length() > 0) {
                ctx.append("\n\n");
            }
            ctx.append("=== THÚ CƯNG CÓ SẴN NHẬN NUÔI ===\n\n");
            ctx.append(buildPets(pets));
        }

        return ctx.toString().trim();
    }

    /**
     * Build context từ danh sách Services
     */
    public String buildServices(List<ServicesPet> services) {
        if (services == null || services.isEmpty()) {
            return "";
        }

        StringBuilder ctx = new StringBuilder();

        for (ServicesPet s : services) {
            // Tên dịch vụ (with link)
            ctx.append("- name: [" + s.getName() + "](/services/" + s.getId() + ")\n");
            ctx.append("  id: ").append(s.getId()).append("\n");

            // Giá
            if (s.getPrice() != null && s.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                ctx.append("  price: ").append(formatPrice(s.getPrice())).append("\n");
            }

            // Thời lượng
            if (s.getDurationMinutes() != null) {
                ctx.append("  duration: ").append(s.getDurationMinutes()).append(" phút\n");
            }

            // Mô tả
            if (s.getDescription() != null && !s.getDescription().isBlank()) {
                ctx.append("  description: ").append(truncate(s.getDescription(), MAX_DESCRIPTION_LEN)).append("\n");
            }

            ctx.append("\n");
        }

        return ctx.toString().trim();
    }

    /**
     * Build context từ danh sách Pets (thú cưng nhận nuôi)
     */
    public String buildPets(List<Pets> pets) {
        if (pets == null || pets.isEmpty()) {
            return "";
        }

        StringBuilder ctx = new StringBuilder();

        for (Pets pet : pets) {
            // Tên thú cưng (with link)
            ctx.append("- name: [" + pet.getName() + "](/pets/" + pet.getId() + ")\n");
            ctx.append("  id: ").append(pet.getId()).append("\n");

            // Loài
            if (pet.getAnimal() != null && !pet.getAnimal().isBlank()) {
                ctx.append("  animal: ").append(pet.getAnimal()).append("\n");
            }

            // Giống
            if (pet.getBreed() != null && !pet.getBreed().isBlank()) {
                ctx.append("  breed: ").append(pet.getBreed()).append("\n");
            }

            // Tuổi
            if (pet.getAge() != null) {
                ctx.append("  age: ").append(pet.getAge()).append(" tuổi");
                if (pet.getAgeGroup() != null && !pet.getAgeGroup().isBlank()) {
                    ctx.append(" (").append(pet.getAgeGroup()).append(")");
                }
                ctx.append("\n");
            }

            // Giới tính
            if (pet.getGender() != null && !pet.getGender().isBlank()) {
                ctx.append("  gender: ").append(pet.getGender()).append("\n");
            }

            // Cân nặng
            if (pet.getWeight() != null) {
                ctx.append("  weight: ").append(pet.getWeight()).append(" kg\n");
            }

            // Tình trạng sức khỏe
            if (pet.getHealthStatus() != null && !pet.getHealthStatus().isBlank()) {
                ctx.append("  health: ").append(pet.getHealthStatus()).append("\n");
            }

            // Tiêm phòng
            if (pet.getVaccinated() != null && !pet.getVaccinated().isBlank()) {
                ctx.append("  vaccinated: ").append(pet.getVaccinated()).append("\n");
            }

            // Triệt sản
            if (pet.getNeutered() != null && !pet.getNeutered().isBlank()) {
                ctx.append("  neutered: ").append(pet.getNeutered()).append("\n");
            }

            // Mô tả
            if (pet.getDescription() != null && !pet.getDescription().isBlank()) {
                ctx.append("  description: ").append(truncate(pet.getDescription(), MAX_DESCRIPTION_LEN)).append("\n");
            }

            ctx.append("\n");
        }

        return ctx.toString().trim();
    }

    public String build(List<Products> products) {
        if (products == null || products.isEmpty()) {
            return "";
        }

        StringBuilder ctx = new StringBuilder();

        for (Products p : products) {
            // Tên sản phẩm (with link) - format: [Tên](/products/{id})
            String productName = cleanProductName(p.getName());
            ctx.append("- name: [" + productName + "](/products/" + p.getId() + ")\n");
            ctx.append("  id: ").append(p.getId()).append("\n");

            // Hiển thị TẤT CẢ variants (tên + giá)
            String variantsInfo = buildVariantsInfo(p);
            if (!variantsInfo.isBlank()) {
                ctx.append("  variants:\n").append(variantsInfo);
            }

            // Brand
            if (p.getBrand() != null && !p.getBrand().isBlank()) {
                ctx.append("  brand: ").append(p.getBrand()).append("\n");
            }

            // Animal
            if (p.getAnimal() != null && !p.getAnimal().isBlank()) {
                ctx.append("  animal: ").append(p.getAnimal()).append("\n");
            }

            // Category
            if (p.getCategory() != null && p.getCategory().getName() != null) {
                ctx.append("  category: ").append(p.getCategory().getName()).append("\n");
            }

            // Description (short)
            if (p.getShortDescription() != null && !p.getShortDescription().isBlank()) {
                ctx.append("  description: ").append(truncate(p.getShortDescription(), MAX_DESCRIPTION_LEN))
                        .append("\n");
            }

            ctx.append("\n");
        }

        return ctx.toString().trim();
    }

    /**
     * Build thông tin variants (tên + giá)
     */
    private String buildVariantsInfo(Products product) {
        try {
            if (product.getProduct_variants() == null) {
                return "";
            }

            var variants = new java.util.ArrayList<>(product.getProduct_variants());

            if (variants.isEmpty()) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (ProductVariants variant : variants) {
                // Skip variant nếu bị xóa
                if ("1".equals(variant.getIsDeleted())) {
                    continue;
                }

                sb.append("    + ").append(variant.getVariantName());

                if (variant.getPrice() != null && variant.getPrice() > 0) {
                    sb.append(" - ").append(formatPrice(variant.getPrice()));
                }

                // Hiển thị stock nếu có
                if (variant.getStockQuantity() > 0) {
                    sb.append(" (còn ").append(variant.getStockQuantity()).append(")");
                } else {
                    sb.append(" (hết hàng)");
                }

                sb.append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            System.out.println("⚠️ Cannot build variants info for product: " + product.getId());
            return "";
        }
    }

    /**
     * Lấy giá thấp nhất từ variants (giữ lại để dùng ở chỗ khác nếu cần)
     */
    private Float getMinPrice(Products product) {
        try {
            if (product.getProduct_variants() == null) {
                return null;
            }

            // Convert Set to List để tránh ConcurrentModificationException
            var variants = new java.util.ArrayList<>(product.getProduct_variants());

            if (variants.isEmpty()) {
                return null;
            }

            return variants.stream()
                    .map(ProductVariants::getPrice)
                    .filter(price -> price != null && price > 0)
                    .min(Float::compareTo)
                    .orElse(null);
        } catch (Exception e) {
            // Lazy loading exception
            System.out.println("⚠️ Cannot get price for product: " + product.getId() + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Loại bỏ prefix brand và mã ID khỏi tên sản phẩm
     */
    private String cleanProductName(String name) {
        if (name == null || name.isBlank())
            return "";

        String cleaned = name;

        // Loại bỏ các brand prefix
        String[] brandPrefixes = {
                "PetCare Việt ",
                "PetCare ",
                "Pet Care ",
                "Royal Canin ",
                "Pedigree "
        };

        for (String prefix : brandPrefixes) {
            if (cleaned.startsWith(prefix)) {
                cleaned = cleaned.substring(prefix.length());
                break;
            }
        }

        // Loại bỏ mã ID dạng #123
        cleaned = cleaned.replaceAll("\\s*#\\d+\\s*$", "");

        return cleaned.trim();
    }

    /**
     * Format giá tiền (Float)
     */
    private String formatPrice(Float price) {
        if (price == null)
            return "";

        long rounded = Math.round(price);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(rounded) + " ₫";
    }

    /**
     * Format giá tiền (BigDecimal)
     */
    private String formatPrice(BigDecimal price) {
        if (price == null)
            return "";

        long rounded = price.longValue();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(rounded) + " ₫";
    }

    /**
     * Truncate string
     */
    private String truncate(String text, int maxLen) {
        if (text == null)
            return "";
        if (text.length() <= maxLen)
            return text;
        return text.substring(0, maxLen) + "...";
    }
}