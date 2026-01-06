package com.webpet_nhom20.backdend.service.Chatbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class GeminiChatService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GeminiChatService.class);
    private final WebClient webClient;
    private final int timeoutSeconds;

    public GeminiChatService(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.chat-url}") String chatUrl,
            @Value("${gemini.timeout-seconds:60}") int timeoutSeconds) {
        this.webClient = WebClient.builder()
                .baseUrl(chatUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("x-goog-api-key", apiKey)
                .build();
        this.timeoutSeconds = timeoutSeconds;
    }

    public String generate(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            return fallback();
        }

        Map<String, Object> body = Map.of(
                "contents", new Object[] {
                        Map.of(
                                "parts", new Object[] {
                                        Map.of("text", prompt)
                                })
                },
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "maxOutputTokens", 1500));

        try {
            Map response = webClient.post()
                    .bodyValue(body)
                    .exchangeToMono(res -> {
                        if (res.statusCode().isError()) {
                            return res.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(err -> Mono.error(
                                            new RuntimeException("Gemini chat failed: "
                                                    + res.statusCode() + " - " + err)));
                        }
                        return res.bodyToMono(Map.class);
                    })
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            if (response == null) {
                return fallback();
            }

            return extractText(response);

        } catch (Exception e) {
            log.warn("GeminiChatService.generate failed: {}", e.toString());
            return fallback();
        }
    }

    private String extractText(Map response) {
        try {
            Object candidatesObj = response.get("candidates");
            if (!(candidatesObj instanceof List) || ((List) candidatesObj).isEmpty()) {
                return fallback();
            }

            Map first = (Map) ((List) candidatesObj).get(0);
            if (first == null)
                return fallback();

            Object finishReason = first.get("finishReason");
            boolean wasTruncated = false;
            if (finishReason != null) {
                log.info("Gemini finishReason = {}", finishReason);
                String reason = finishReason.toString();
                if ("MAX_TOKENS".equals(reason) || "LENGTH".equals(reason)) {
                    wasTruncated = true;
                    log.warn("Response was truncated due to: {}", reason);
                }
            }

            Object contentObj = first.get("content");
            if (!(contentObj instanceof Map))
                return fallback();

            Object partsObj = ((Map) contentObj).get("parts");
            if (!(partsObj instanceof List) || ((List) partsObj).isEmpty()) {
                return fallback();
            }

            StringBuilder sb = new StringBuilder();
            for (Object part : (List) partsObj) {
                if (!(part instanceof Map))
                    continue;
                Object text = ((Map) part).get("text");
                if (text != null) {
                    sb.append(text);
                }
            }

            if (sb.length() == 0) {
                log.warn("Gemini response has no text parts: {}", response);
                return fallback();
            }

            String result = sb.toString().trim();

            // Nếu bị cắt ngang và không kết thúc bằng dấu câu, thêm dấu chấm
            if (wasTruncated && result.length() > 0) {
                char lastChar = result.charAt(result.length() - 1);
                if (!isPunctuationEnd(lastChar)) {
                    // Tìm vị trí dấu câu cuối cùng và cắt từ đó
                    int lastPunctuation = Math.max(
                            Math.max(result.lastIndexOf('.'), result.lastIndexOf('!')),
                            Math.max(result.lastIndexOf('?'), result.lastIndexOf('。')));
                    if (lastPunctuation > result.length() / 2) {
                        // Nếu tìm thấy dấu câu ở nửa sau, cắt từ đó
                        result = result.substring(0, lastPunctuation + 1);
                    } else {
                        // Nếu không, thêm "..." để báo hiệu chưa xong
                        result += "...";
                    }
                }
            }

            return result;

        } catch (Exception e) {
            log.warn("Gemini response parse failed: {}", e.toString());
            return fallback();
        }
    }

    private boolean isPunctuationEnd(char c) {
        return c == '.' || c == '!' || c == '?' || c == '。' || c == '！' || c == '？';
    }

    private String fallback() {
        return "Hiện tại hệ thống đang bận. Bạn vui lòng thử lại sau hoặc hỏi theo cách khác nhé.";
    }
}