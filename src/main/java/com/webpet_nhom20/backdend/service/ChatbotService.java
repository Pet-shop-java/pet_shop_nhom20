package com.webpet_nhom20.backdend.service;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatbotService {
    private final GeminiEmbeddingService embeddingService;
    private final QdrantService qdrantService;
    private final GeminiChatService chatService;

    public ChatbotService(
            GeminiEmbeddingService embeddingService,
            QdrantService qdrantService,
            GeminiChatService chatService
    ) {
        this.embeddingService = embeddingService;
        this.qdrantService = qdrantService;
        this.chatService = chatService;
    }

    public String ask(String question) {

        var vector = embeddingService.embedText(question);

        List<String> contexts =
                qdrantService.searchTopContents(vector, 3);

        String contextBlock = String.join("\n", contexts);

        String prompt = """
Bạn là chatbot tư vấn của cửa hàng PetShop.

QUY TẮC BẮT BUỘC:
- Chỉ sử dụng thông tin bên dưới để trả lời.
- Tuyệt đối không suy đoán hoặc thêm kiến thức bên ngoài.
- Nếu không có thông tin phù hợp, hãy nói rõ là không biết.

CÁCH TRẢ LỜI:
- Trả lời đúng trọng tâm câu hỏi.
- Nếu có sản phẩm phù hợp, có thể gợi ý nhẹ nhàng.
- Giữ giọng thân thiện, chuyên nghiệp.

--------------------
DỮ LIỆU PETSHOP:
%s
--------------------

CÂU HỎI:
%s
""".formatted(contextBlock, question);



        return chatService.chat(prompt);
    }
}
