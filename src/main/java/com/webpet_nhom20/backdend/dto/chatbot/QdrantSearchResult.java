package com.webpet_nhom20.backdend.dto.chatbot;

import java.util.Map;

public record QdrantSearchResult(
        float score,
        Map<String, Object> payload
) {}

