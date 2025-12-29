package com.webpet_nhom20.backdend.dto.chatbot;

import lombok.Data;

@Data
public class ImportRow {
    private String id;
    private String type;
    private String status;
    private String name;

    private String animal;
    private String category;
    private String brand;
    private Double price;

    private String keyFeatures;
    private String warnings;
    private String source;

    public boolean isValid() {
        return id != null && !id.isBlank()
                && type != null && !type.isBlank()
                && status != null && !status.isBlank()
                && name != null && !name.isBlank();
}
}
