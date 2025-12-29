package com.webpet_nhom20.backdend.dto.chatbot;

public class ChatMessage {
    private String role;
    private String text;

    public ChatMessage() {
    }

    public ChatMessage(String role, String text) {
        this.role = role;
        this.text = text;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
