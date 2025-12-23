package com.webpet_nhom20.backdend.dto.request.Chat;

import java.util.List;

public class GeminiEmbeddingRequest {
    private Content content;

    public GeminiEmbeddingRequest(String text) {
        this.content = new Content(
                List.of(new Part(text))
        );
    }

    public Content getContent() {
        return content;
    }

    // ===== INNER CLASSES =====

    public static class Content {
        private List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }

        public List<Part> getParts() {
            return parts;
        }
    }

    public static class Part {
        private String text;

        public Part(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
