package com.webpet_nhom20.backdend.dto.response.Chat;

import java.util.List;

public class GeminiEmbeddingResponse {
    private Embedding embedding;

    public Embedding getEmbedding() {
        return embedding;
    }

    public static class Embedding {
        private List<Float> values;

        public List<Float> getValues() {
            return values;
        }
    }
}
