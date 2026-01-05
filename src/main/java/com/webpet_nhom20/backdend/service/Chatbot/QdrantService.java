package com.webpet_nhom20.backdend.service.Chatbot;


import com.webpet_nhom20.backdend.dto.chatbot.QdrantPoint;
import com.webpet_nhom20.backdend.dto.chatbot.QdrantSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class QdrantService {
    private static final Logger log = LoggerFactory.getLogger(QdrantService.class);

    private final WebClient webClient;
    private final String collection;

    public QdrantService(
            @Value("${qdrant.url}") String baseUrl,
            @Value("${qdrant.collection}") String collection,
            @Value("${qdrant.api-key:}") String apiKey
    ) {
        this.collection = collection;

        WebClient.Builder b = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        if (apiKey != null && !apiKey.isBlank()) {
            b.defaultHeader("api-key", apiKey); // Qdrant Cloud
        }

        this.webClient = b.build();
    }

    public void upsertPoints(List<QdrantPoint> points) {
        Map<String, Object> body = new HashMap<>();
        body.put("points", points);

        webClient.put()
                .uri("/collections/{collection}/points?wait=true", collection)
                .bodyValue(body)
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(bodyText -> Mono.error(
                                        new RuntimeException("Qdrant upsert failed: "
                                                + response.statusCode() + " - " + bodyText)
                                ));
                    }
                    return response.bodyToMono(String.class);
                })
                .block();
    }

    // ✅ SEARCH: đặt NGAY trong QdrantService (cùng class)
    public List<QdrantSearchResult> search(
            float[] vector,
            int limit,
            Double scoreThreshold,
            Map<String, Object> filters
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("vector", vector);
        body.put("limit", limit);
        body.put("with_payload", true);
        body.put("with_vector", false);
        if (scoreThreshold != null && scoreThreshold > 0) {
            body.put("score_threshold", scoreThreshold);
        }

        if (filters != null && !filters.isEmpty()) {
            List<Map<String, Object>> must = new ArrayList<>();
            for (Map.Entry<String, Object> e : filters.entrySet()) {
                Map<String, Object> match =
                        (e.getValue() instanceof String)
                                ? Map.of("text", e.getValue())
                                : Map.of("value", e.getValue());
                must.add(Map.of(
                        "key", e.getKey(),
                        "match", match
                ));
            }
            body.put("filter", Map.of("must", must));
        }

        Map<String, Object> response = webClient.post()
                .uri("/collections/{collection}/points/search", collection)
                .bodyValue(body)
                .exchangeToMono(res -> {
                    if (res.statusCode().isError()) {
                        return res.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(txt -> Mono.error(
                                        new RuntimeException("Qdrant search failed: "
                                                + res.statusCode() + " - " + txt)
                                ));
                    }
                    return res.bodyToMono(Map.class);
                })
                .block();

        if (response == null) return List.of();

        List<Map<String, Object>> result =
                (List<Map<String, Object>>) response.getOrDefault("result", List.of());

        List<QdrantSearchResult> list = new ArrayList<>();
        for (Map<String, Object> r : result) {
            float score = ((Number) r.get("score")).floatValue();
            Map<String, Object> payload = (Map<String, Object>) r.get("payload");
            list.add(new QdrantSearchResult(score, payload));
        }
        return list;
    }
}
