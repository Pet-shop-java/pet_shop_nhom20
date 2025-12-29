package com.webpet_nhom20.backdend.dto.chatbot;

public class DecisionMessages {
    public static String outOfScope() {
        return "Hiện tại mình chưa có thông tin phù hợp trong hệ thống.";
    }

    public static String lowConfidence() {
        return "Mình chưa chắc thông tin này. Bạn nói rõ hơn được không?";
    }

    public static String ambiguous() {
        return "Bạn có thể nói rõ hơn bạn đang hỏi về sản phẩm hoặc dịch vụ nào không?";
    }
}
