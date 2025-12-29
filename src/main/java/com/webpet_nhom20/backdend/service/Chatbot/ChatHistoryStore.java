package com.webpet_nhom20.backdend.service.Chatbot;



import com.webpet_nhom20.backdend.dto.chatbot.ChatMessage;

import java.util.List;

public interface ChatHistoryStore {
    List<ChatMessage> getHistory(String sessionId, int limit);

    void append(String sessionId, ChatMessage message);

    void appendAll(String sessionId, List<ChatMessage> messages);
}
