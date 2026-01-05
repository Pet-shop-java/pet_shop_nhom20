package com.webpet_nhom20.backdend.service.Chatbot;

import com.webpet_nhom20.backdend.dto.chatbot.ChatMessage;
import com.webpet_nhom20.backdend.dto.chatbot.ChatResponse;
import com.webpet_nhom20.backdend.dto.chatbot.QdrantSearchResult;
import com.webpet_nhom20.backdend.enums.DecistionType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChatbotService {
    private static final int HISTORY_LIMIT = 8;
    private final EmbeddingService embeddingService;
    private final QdrantService qdrantService;
    private final DecisionService decisionService;
    private final ContextBuilder contextBuilder;
    private final PromptBuilder promptBuilder;
    private final GeminiChatService geminiChatService;
    private final ChatHistoryStore chatHistoryStore;

    public ChatbotService(
            EmbeddingService embeddingService,
            QdrantService qdrantService,
            DecisionService decisionService,
            ContextBuilder contextBuilder,
            PromptBuilder promptBuilder,
            GeminiChatService geminiChatService,
            ChatHistoryStore chatHistoryStore) {
        this.embeddingService = embeddingService;
        this.qdrantService = qdrantService;
        this.decisionService = decisionService;
        this.contextBuilder = contextBuilder;
        this.promptBuilder = promptBuilder;
        this.geminiChatService = geminiChatService;
        this.chatHistoryStore = chatHistoryStore;
    }

    public ChatResponse ask(String question) {
        return askWithHistory(null, question, null);
    }

    public ChatResponse askWithHistory(String sessionId, String question, List<ChatMessage> history) {
        String sid = (sessionId == null || sessionId.isBlank())
                ? UUID.randomUUID().toString()
                : sessionId;

        // 1️⃣ Embed câu hỏi
        float[] queryVector = embeddingService.embed(question);

        // 2️⃣ Search Qdrant (chưa filter để tránh lỗi index)
        List<QdrantSearchResult> results;
        try {
            results = qdrantService.search(
                    queryVector,
                    3, // topK
                    null, // score threshold (tắt để debug empty results)
                    buildFilters(question));
        } catch (RuntimeException e) {
            if (isMissingIndexError(e)) {
                results = qdrantService.search(
                        queryVector,
                        3,
                        null,
                        null);
            } else {
                throw e;
            }
        }

        System.out.println("QDRANT RESULTS SIZE = " + results.size());
        if (!results.isEmpty()) {
            System.out.println("QDRANT TOP SCORE = " + results.get(0).score());
        }
        // 3️⃣ Decision / Confidence Check
        List<ChatMessage> storedHistory = chatHistoryStore.getHistory(sid, HISTORY_LIMIT);
        List<ChatMessage> historyForPrompt = (history != null && !history.isEmpty())
                ? history
                : storedHistory;

        DecistionType decision = decisionService.decide(question, results, historyForPrompt);
        System.out.println("DECISION = " + decision);

        String context = contextBuilder.build(results);
        String historyText = formatHistory(historyForPrompt);

        // 4️⃣ Trả lời NGAY – KHÔNG GỌI LLM
        if (decision != DecistionType.ALLOW_IG) {
            String answer = switch (decision) {
                case OUT_OF_SCOPE -> "Hiện tại mình chưa có thông tin phù hợp trong hệ thống.";
                case LOW_CONFIDENCE -> "Mình chưa chắc thông tin này, bạn nói rõ hơn được không?";
                case AMBIGUOUS -> "Bạn nói rõ hơn bạn đang hỏi về sản phẩm nào nhé.";
                default -> "Không xác định.";
            };
            persistHistoryIfNeeded(sid, storedHistory, history, question, answer);
            return new ChatResponse(answer, context, sid);
        }

        String prompt = promptBuilder.build(context, question, historyText);
        String answer = geminiChatService.generate(prompt);
        persistHistoryIfNeeded(sid, storedHistory, history, question, answer);
        return new ChatResponse(answer, context, sid);
    }

    private java.util.Map<String, Object> buildFilters(String question) {
        if (question == null)
            return null;
        String q = question.toLowerCase();

        // Filter theo loài (ưu tiên cao nhất)
        if (q.contains("chó") || q.contains("cún") || q.contains("dog")) {
            return java.util.Map.of("animal", "chó");
        }
        if (q.contains("mèo") || q.contains("cat") || q.contains("meo")) {
            return java.util.Map.of("animal", "mèo");
        }
        if (q.contains("cá") || q.contains("fish")) {
            return java.util.Map.of("animal", "cá");
        }

        // Filter theo category
        if (q.contains("cát vệ sinh")) {
            return java.util.Map.of("category", "cát vệ sinh");
        }

        return null;
    }

    private boolean isMissingIndexError(RuntimeException e) {
        String msg = e.getMessage();
        return msg != null && msg.contains("Index required");
    }

    private String formatHistory(List<ChatMessage> history) {
        if (history == null || history.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        int from = Math.max(0, history.size() - HISTORY_LIMIT);
        for (int i = from; i < history.size(); i++) {
            ChatMessage m = history.get(i);
            if (m == null)
                continue;
            String role = m.getRole() == null ? "unknown" : m.getRole().trim();
            String text = m.getText() == null ? "" : m.getText().trim();
            if (text.isEmpty())
                continue;
            sb.append(role).append(": ").append(text).append("\n");
        }
        return sb.toString().trim();
    }

    private void persistHistoryIfNeeded(
            String sessionId,
            List<ChatMessage> storedHistory,
            List<ChatMessage> historyFromClient,
            String question,
            String answer) {
        if ((storedHistory == null || storedHistory.isEmpty())
                && historyFromClient != null && !historyFromClient.isEmpty()) {
            chatHistoryStore.appendAll(sessionId, historyFromClient);
        }
        chatHistoryStore.append(sessionId, new ChatMessage("user", question));
        chatHistoryStore.append(sessionId, new ChatMessage("assistant", answer));
    }

}
