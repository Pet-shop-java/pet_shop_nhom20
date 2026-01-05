package com.webpet_nhom20.backdend.service.Chatbot;


import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public interface EmbeddingService {
    float[] embed(String text);
}
