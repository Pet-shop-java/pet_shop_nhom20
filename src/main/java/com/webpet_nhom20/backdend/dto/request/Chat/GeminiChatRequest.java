package com.webpet_nhom20.backdend.dto.request.Chat;

import java.util.List;

public class GeminiChatRequest {
    private List<Content> contents;

    public GeminiChatRequest(String prompt) {
        this.contents = List.of(
                new Content(
                        List.of(new Part(prompt))
                )
        );
    }

    public List<Content> getContents() {
        return contents;
    }

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
