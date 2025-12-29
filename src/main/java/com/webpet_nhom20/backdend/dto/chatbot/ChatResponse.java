package com.webpet_nhom20.backdend.dto.chatbot;

public class ChatResponse {
    private final String answer;
    private final String context;
    private final String sessionId;

    public ChatResponse(String answer, String context, String sessionId) {
        this.answer = answer;
        this.context = context;
        this.sessionId = sessionId;
    }

    public String getAnswer() {
        return answer;
    }

    public String getContext() {
        return context;
    }

    public String getSessionId() {
        return sessionId;
    }
}
