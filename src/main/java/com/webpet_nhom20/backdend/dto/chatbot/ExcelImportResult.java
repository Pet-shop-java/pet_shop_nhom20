package com.webpet_nhom20.backdend.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ExcelImportResult {
    private int success;
    private int skipped;
    private List<String> errors;
}
