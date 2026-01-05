package com.webpet_nhom20.backdend.dto.chatbot;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductVectorPayload {
    private String type;      // product / service
    private String animal;    // dog / cat
    private String category;  // food / toy / litter
    private String brand;
    private double price;
    private String status;    // active / inactive

    // context fields
    private String name;
    private String features;
    private String warnings;

}
