package com.webpet_nhom20.backdend.service.Chatbot;


import com.webpet_nhom20.backdend.dto.chatbot.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryChatHistoryStore implements ChatHistoryStore {
    private final Map<String, List<ChatMessage>> store = new ConcurrentHashMap<>();

    @Override
    public List<ChatMessage> getHistory(String sessionId, int limit) {
        if (sessionId == null || sessionId.isBlank()) {
            return List.of();
        }
        List<ChatMessage> list = store.getOrDefault(sessionId, List.of());
        if (list.isEmpty()) return List.of();
        int from = Math.max(0, list.size() - limit);
        return new ArrayList<>(list.subList(from, list.size()));
    }

    @Override
    public void append(String sessionId, ChatMessage message) {
        if (sessionId == null || sessionId.isBlank() || message == null) return;
        store.compute(sessionId, (k, v) -> {
            List<ChatMessage> list = (v == null) ? new ArrayList<>() : v;
            list.add(message);
            return list;
        });
    }

    @Override
    public void appendAll(String sessionId, List<ChatMessage> messages) {
        if (sessionId == null || sessionId.isBlank() || messages == null || messages.isEmpty()) {
            return;
        }
        store.compute(sessionId, (k, v) -> {
            List<ChatMessage> list = (v == null) ? new ArrayList<>() : v;
            list.addAll(messages);
            return list;
        });
    }
}