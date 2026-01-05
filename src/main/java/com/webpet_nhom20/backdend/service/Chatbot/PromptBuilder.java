package com.webpet_nhom20.backdend.service.Chatbot;


import com.webpet_nhom20.backdend.Prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
public class PromptBuilder {
    public String build(String context, String question, String history) {
        return String.format(
                PromptTemplate.RUNTIME_PROMPT,
                context == null || context.isBlank() ? "Không có dữ liệu phù hợp." : context,
                history == null || history.isBlank() ? "(không có)" : history,
                question
        );
    }
}