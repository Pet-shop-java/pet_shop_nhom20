package com.webpet_nhom20.backdend.service;


import com.webpet_nhom20.backdend.dto.request.Chat.GeminiChatRequest;
import com.webpet_nhom20.backdend.dto.response.Chat.GeminiChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiChatService {
    private final WebClient webClient;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.chat-url}")
    private String chatUrl;

    public GeminiChatService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String chat(String prompt) {

        GeminiChatRequest request =
                new GeminiChatRequest(prompt);

        GeminiChatResponse response = webClient
                .post()
                .uri(chatUrl + "?key=" + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiChatResponse.class)
                .block();

        if (response == null ||
                response.getCandidates() == null ||
                response.getCandidates().isEmpty()) {
            return "Xin lỗi, tôi chưa thể trả lời câu hỏi này.";
        }

        return response
                .getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
    }
}
