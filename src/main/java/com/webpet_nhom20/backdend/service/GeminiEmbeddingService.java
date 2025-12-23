package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.Chat.GeminiEmbeddingRequest;
import com.webpet_nhom20.backdend.dto.response.Chat.GeminiEmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GeminiEmbeddingService {
    private final WebClient webClient;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.embed-url}")
    private String embeddingUrl;

    public GeminiEmbeddingService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Float> embedText(String text) {

        GeminiEmbeddingRequest request =
                new GeminiEmbeddingRequest(text);

        GeminiEmbeddingResponse response = webClient
                .post()
                .uri(embeddingUrl + "?key=" + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiEmbeddingResponse.class)
                .block();

        if (response == null || response.getEmbedding() == null) {
            throw new RuntimeException("Gemini embedding response is null");
        }

        return response.getEmbedding().getValues();
    }
}
