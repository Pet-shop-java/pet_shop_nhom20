package com.webpet_nhom20.backdend.dto.response.Adopt;

import lombok.*;

import java.util.Date;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AdoptDetailResponse {

    private Integer adoptId;
    private String code;
    private String status;
    private String note;
    private String job;
    private String income;
    private String isOwnPet;
    private String liveCondition;
    private Date createdDate;

    private Pet pet;
    private Applicant applicant;

    @Getter @Builder
    public static class Pet {
        private Integer id;
        private String name;
        private String animal;
        private String breed;
        private Integer age;
        private String size;
        private String gender;
        private String image;
    }

    @Getter @Builder
    public static class Applicant {
        private Integer userId;
        private String fullName;
        private String phone;
        private String address;
    }
}



