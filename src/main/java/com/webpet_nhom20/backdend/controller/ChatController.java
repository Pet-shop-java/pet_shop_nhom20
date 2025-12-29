package com.webpet_nhom20.backdend.controller;


import com.webpet_nhom20.backdend.dto.chatbot.ChatRequest;
import com.webpet_nhom20.backdend.dto.chatbot.ChatResponse;
import com.webpet_nhom20.backdend.dto.chatbot.ChatWithHistoryRequest;
import com.webpet_nhom20.backdend.service.Chatbot.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatbotService chatbotService;

    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody ChatRequest request) {
        if (request == null || request.getQuestion() == null || request.getQuestion().isBlank()) {
            return ResponseEntity.badRequest().body("Question is required");
        }

        ChatResponse response = chatbotService.ask(request.getQuestion());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ask-with-history")
    public ResponseEntity<?> askWithHistory(@RequestBody ChatWithHistoryRequest request) {
        if (request == null || request.getQuestion() == null || request.getQuestion().isBlank()) {
            return ResponseEntity.badRequest().body("Question is required");
        }

        ChatResponse response = chatbotService.askWithHistory(
                request.getSessionId(),
                request.getQuestion(),
                request.getHistory()
        );
        return ResponseEntity.ok(response);
    }
}
