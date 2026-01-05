package com.webpet_nhom20.backdend.service.Chatbot;

import com.webpet_nhom20.backdend.dto.chatbot.QdrantSearchResult;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Map;
import java.util.Locale;

@Service
public class ContextBuilder {

    private static final int MAX_ITEMS = 3;
    private static final int MAX_FIELD_LEN = 300;

    public String build(List<QdrantSearchResult> results) {
        if (results == null || results.isEmpty()) {
            return "";
        }

        StringBuilder ctx = new StringBuilder();

        int count = 0;
        for (QdrantSearchResult r : results) {
            if (count >= MAX_ITEMS)
                break;

            Map<String, Object> p = r.payload();
            if (p == null || p.isEmpty()) {
                continue;
            }

            ctx.append("- name: ").append(cleanProductName(val(p, "name"))).append("\n");

            if (p.containsKey("price"))
                ctx.append("  price: ").append(formatPrice(p.get("price"))).append("\n");

            if (has(p, "brand"))
                ctx.append("  brand: ").append(val(p, "brand")).append("\n");

            if (has(p, "category"))
                ctx.append("  category: ").append(val(p, "category")).append("\n");

            if (has(p, "animal"))
                ctx.append("  animal: ").append(val(p, "animal")).append("\n");

            if (has(p, "key_features"))
                ctx.append("  features: ").append(val(p, "key_features")).append("\n");

            if (has(p, "warnings"))
                ctx.append("  warnings: ").append(val(p, "warnings")).append("\n");

            ctx.append("\n");
            count++;
        }

        return ctx.toString().trim();
    }

    private boolean has(Map<String, Object> p, String k) {
        Object v = p.get(k);
        return v != null && !String.valueOf(v).isBlank();
    }

    private String val(Map<String, Object> p, String k) {
        return val(p, k, MAX_FIELD_LEN);
    }

    private String val(Map<String, Object> p, String k, int maxLen) {
        Object v = p.get(k);
        if (v == null)
            return "";
        String s = String.valueOf(v);
        if (s.length() <= maxLen)
            return s;
        return s.substring(0, maxLen) + "...";
    }

    /**
     * Loại bỏ prefix brand (PetCare Việt, etc) và mã ID (#xxx) để tiết kiệm token
     */
    private String cleanProductName(String name) {
        if (name == null || name.isBlank())
            return "";

        String cleaned = name;

        // Loại bỏ các brand prefix phổ biến
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

        // Loại bỏ mã ID dạng #123 ở cuối
        cleaned = cleaned.replaceAll("\\s*#\\d+\\s*$", "");

        return cleaned.trim();
    }

    private String formatPrice(Object price) {
        if (price == null)
            return "";
        if (!(price instanceof Number))
            return String.valueOf(price);

        long rounded = Math.round(((Number) price).doubleValue());
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(rounded) + " ₫";
    }
}
