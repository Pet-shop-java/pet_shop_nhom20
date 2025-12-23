package com.webpet_nhom20.backdend.dto.request.Qdrant;

import java.util.List;

public class QdrantSearchRequest {
    private List<Float> vector;
    private int limit;
    private boolean with_payload = true;

    public QdrantSearchRequest(List<Float> vector, int limit) {
        this.vector = vector;
        this.limit = limit;
    }

    public List<Float> getVector() {
        return vector;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isWith_payload() {
        return with_payload;
    }
}
