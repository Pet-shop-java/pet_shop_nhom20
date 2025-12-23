package com.webpet_nhom20.backdend.dto.response.PetImage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PetImageResponse {
    int id;
    int petId;
    String imageUrl;
    int position = 0;
    int isPrimary = 0;
    String isDeleted = "0";
    @JsonFormat(pattern = "yyyy-MM-dd ")
    Date createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd ")
    Date updatedDate;
}
