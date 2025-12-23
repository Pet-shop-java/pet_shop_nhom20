package com.webpet_nhom20.backdend.dto.response.Pet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.webpet_nhom20.backdend.dto.response.PetImage.PetImageResponse;
import com.webpet_nhom20.backdend.entity.PetImages;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PetResponse {
    private Integer id;
    private String name;
    private String animal; // Dog, Cat
    private String breed;
    private Integer age;
    private String ageGroup;
    private String size;
    private String gender;
    private String description;
    private String healthStatus;
    private String vaccinated;
    private String neutered;
    String isDeleted ;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date updatedDate;
    private List<PetImageResponse> images;
}
