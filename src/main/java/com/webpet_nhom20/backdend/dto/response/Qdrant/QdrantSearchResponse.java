package com.webpet_nhom20.backdend.dto.response.Qdrant;

import java.util.List;
import java.util.Map;

public class QdrantSearchResponse {
    private List<Result> result;

    public List<Result> getResult() {
        return result;
    }

    public static class Result {
        private float score;
        private Map<String, Object> payload;

        public float getScore() {
            return score;
        }

        public Map<String, Object> getPayload() {
            return payload;
        }
    }
}
