package com.webpet_nhom20.backdend.dto.chatbot;

import java.util.List;

public class ChatWithHistoryRequest {
    private String sessionId;
    private String question;
    private List<ChatMessage> history;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<ChatMessage> getHistory() {
        return history;
    }

    public void setHistory(List<ChatMessage> history) {
        this.history = history;
    }
}
