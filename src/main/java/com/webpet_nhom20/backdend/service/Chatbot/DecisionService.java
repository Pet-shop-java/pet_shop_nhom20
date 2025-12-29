package com.webpet_nhom20.backdend.service.Chatbot;


import com.webpet_nhom20.backdend.dto.chatbot.QdrantSearchResult;
import com.webpet_nhom20.backdend.enums.DecistionType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecisionService {
    private static final double SCORE_THRESHOLD = 0.4;

    public DecistionType decide(
            String question,
            List<QdrantSearchResult> results
    ) {



        // Rule 1: không có kết quả
        if (results == null || results.isEmpty()) {
            return DecistionType.OUT_OF_SCOPE;
        }

        // Rule 2: score thấp
        float topScore = results.get(0).score();
        if (topScore < SCORE_THRESHOLD) {
            return DecistionType.LOW_CONFIDENCE;
        }

        // Rule 3: câu hỏi mơ hồ
        if (isAmbiguous(question)) {
            return DecistionType.AMBIGUOUS;
        }

        // Rule 4: cho phép IG
        return DecistionType.ALLOW_IG;
    }

    private boolean isAmbiguous(String q) {
        if (q == null) return true;

        String s = q.trim().toLowerCase();

        // quá ngắn
        if (s.length() < 5) return true;

        // các pattern mơ hồ (đại từ trỏ, thiếu chủ ngữ rõ ràng)
        return s.matches("^(cái này|loại này|này|đó|cái đó).*$");
    }
}
