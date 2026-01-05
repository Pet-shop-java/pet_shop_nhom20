package com.webpet_nhom20.backdend.service.Chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeminiEmbeddingService implements EmbeddingService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String embedUrl;

    // 🔥 cache để tránh gọi lại
    private final Map<String, float[]> cache = new ConcurrentHashMap<>();

    public GeminiEmbeddingService(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.embed-url}") String embedUrl
    ) {
        this.webClient = WebClient.builder()
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("x-goog-api-key", apiKey)
                .build();
        this.embedUrl = embedUrl;
    }

    @Override
    public float[] embed(String text) {

        // 1️⃣ cache hit
        if (cache.containsKey(text)) {
            return cache.get(text);
        }

        // 2️⃣ build request
        Map<String, Object> body = Map.of(
                "content", Map.of(
                        "parts", new Object[]{
                                Map.of("text", text)
                        }
                )
        );

        // 3️⃣ call Gemini
        String response = webClient.post()
                .uri(embedUrl)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode values = root
                    .path("embedding")
                    .path("values");

            float[] vector = new float[values.size()];
            for (int i = 0; i < values.size(); i++) {
                vector[i] = values.get(i).floatValue();
            }

            // 4️⃣ save cache
            cache.put(text, vector);

            return vector;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse embedding response", e);
        }
    }
}
