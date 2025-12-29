package com.webpet_nhom20.backdend.config;


import com.webpet_nhom20.backdend.service.Chatbot.ChatHistoryStore;
import com.webpet_nhom20.backdend.service.Chatbot.InMemoryChatHistoryStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatHistoryConfig {
    @Bean
    public ChatHistoryStore chatHistoryStore() {
        return new InMemoryChatHistoryStore();
    }
}
