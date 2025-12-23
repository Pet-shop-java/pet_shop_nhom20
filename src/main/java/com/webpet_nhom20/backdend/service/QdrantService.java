package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.Qdrant.QdrantSearchRequest;
import com.webpet_nhom20.backdend.dto.response.Qdrant.QdrantSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QdrantService {
    private final WebClient webClient;

    @Value("${qdrant.url}")
    private String qdrantUrl;

    @Value("${qdrant.api-key}")
    private String apiKey;

    @Value("${qdrant.collection}")
    private String collection;

    public QdrantService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<String> searchTopContents(List<Float> vector, int topK) {

        QdrantSearchRequest request =
                new QdrantSearchRequest(vector, topK);

        QdrantSearchResponse response = webClient
                .post()
                .uri(qdrantUrl + "/collections/" + collection + "/points/search")
                .header("api-key", apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(QdrantSearchResponse.class)
                .block();

        if (response == null || response.getResult() == null) {
            return List.of();
        }

        return response.getResult()
                .stream()
                .map(r -> (String) r.getPayload().get("content"))
                .collect(Collectors.toList());
    }
}
