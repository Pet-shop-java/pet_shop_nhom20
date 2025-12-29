package com.webpet_nhom20.backdend.dto.chatbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QdrantPoint {
    public Object id;
    public List<Float> vector;
    public Map<String, Object> payload;

    public QdrantPoint(Object id, float[] vector, Map<String, Object> payload) {
        this.id = id;
        this.vector = new ArrayList<>(vector.length);
        for (float v : vector) {
            this.vector.add(v);
        }
        this.payload = payload;
    }
}
