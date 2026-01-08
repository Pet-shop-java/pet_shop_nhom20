package com.webpet_nhom20.backdend.service.Chatbot;

import com.webpet_nhom20.backdend.dto.chatbot.ChatMessage;
import com.webpet_nhom20.backdend.dto.chatbot.ChatResponse;
import com.webpet_nhom20.backdend.entity.Products;
import com.webpet_nhom20.backdend.enums.DecistionType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChatbotService {
    private static final int HISTORY_LIMIT = 8;
    private final ProductQueryService productQueryService;
    private final DBContextBuilder dbContextBuilder;
    private final DecisionService decisionService;
    private final PromptBuilder promptBuilder;
    private final GeminiChatService geminiChatService;
    private final ChatHistoryStore chatHistoryStore;

    public ChatbotService(
            ProductQueryService productQueryService,
            DBContextBuilder dbContextBuilder,
            DecisionService decisionService,
            PromptBuilder promptBuilder,
            GeminiChatService geminiChatService,
            ChatHistoryStore chatHistoryStore) {
        this.productQueryService = productQueryService;
        this.dbContextBuilder = dbContextBuilder;
        this.decisionService = decisionService;
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

        // 1️⃣ Query sản phẩm, dịch vụ VÀ thú cưng từ DB
        List<Products> products = productQueryService.searchProducts(question, 5);
        List<com.webpet_nhom20.backdend.entity.ServicesPet> services = productQueryService.searchServices(5);
        List<com.webpet_nhom20.backdend.entity.Pets> pets = productQueryService.searchPets(5);

        System.out.println("DB QUERY: " + products.size() + " products, " + services.size() + " services, "
                + pets.size() + " pets");

        // 2️⃣ Decision / Confidence Check
        List<ChatMessage> storedHistory = chatHistoryStore.getHistory(sid, HISTORY_LIMIT);
        List<ChatMessage> historyForPrompt = (history != null && !history.isEmpty())
                ? history
                : storedHistory;

        // Check tất cả nguồn data
        DecistionType decision = (products.isEmpty() && services.isEmpty() && pets.isEmpty())
                ? DecistionType.OUT_OF_SCOPE
                : DecistionType.ALLOW_IG;
        System.out.println("DECISION = " + decision);

        // Build context từ sản phẩm, dịch vụ VÀ thú cưng
        String context = dbContextBuilder.buildCombined(products, services, pets);
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