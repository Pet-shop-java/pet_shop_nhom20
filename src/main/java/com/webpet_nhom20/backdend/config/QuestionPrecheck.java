package com.webpet_nhom20.backdend.config;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class QuestionPrecheck {
    public Optional<String> precheck(String question) {

        if (question.length() < 5) {
            return Optional.of("Bạn có thể mô tả rõ hơn nhu cầu cho thú cưng không?");
        }

        if (question.matches(".*(giá rẻ nhất|tốt nhất|so sánh tất cả).*")) {
            return Optional.of("Bạn đang nuôi thú cưng nào (chó/mèo) và ngân sách khoảng bao nhiêu?");
        }

        if (question.contains("thuốc")) {
            return Optional.of("Tôi không thể tư vấn thuốc. Bạn nên hỏi bác sĩ thú y.");
        }

        return Optional.empty();
    }
}
