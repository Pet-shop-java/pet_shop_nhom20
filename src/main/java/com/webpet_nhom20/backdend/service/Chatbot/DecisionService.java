package com.webpet_nhom20.backdend.service.Chatbot;

import com.webpet_nhom20.backdend.dto.chatbot.ChatMessage;
import com.webpet_nhom20.backdend.dto.chatbot.QdrantSearchResult;
import com.webpet_nhom20.backdend.enums.DecistionType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecisionService {
    private static final double SCORE_THRESHOLD = 0.4;

    public DecistionType decide(
            String question,
            List<QdrantSearchResult> results,
            List<ChatMessage> history) {

        // Rule 1: không có kết quả
        if (results == null || results.isEmpty()) {
            return DecistionType.OUT_OF_SCOPE;
        }

        // Rule 2: score thấp
        float topScore = results.get(0).score();
        if (topScore < SCORE_THRESHOLD) {
            return DecistionType.LOW_CONFIDENCE;
        }

        // Rule 3: câu hỏi mơ hồ (nhưng cho phép nếu có history)
        if (isAmbiguous(question, history)) {
            return DecistionType.AMBIGUOUS;
        }

        // Rule 4: cho phép IG
        return DecistionType.ALLOW_IG;
    }

    private boolean isAmbiguous(String q, List<ChatMessage> history) {
        if (q == null)
            return true;

        String s = q.trim().toLowerCase();

        // Nếu có history (conversation đang diễn ra) → cho phép câu follow-up
        boolean hasHistory = history != null && !history.isEmpty();
        if (hasHistory) {
            // Với history, chỉ reject câu QUÁ NGẮN (< 3 từ)
            return s.split("\\s+").length < 3;
        }

        // Không có history: câu quá ngắn → ambiguous
        if (s.length() < 5)
            return true;

        // Câu bắt đầu bằng đại từ trỏ MÀ KHÔNG CÓ history → ambiguous
        return s.matches("^(cái này|loại này|này|đó|cái đó).*$");
    }
}
