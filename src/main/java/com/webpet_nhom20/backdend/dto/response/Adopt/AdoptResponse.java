package com.webpet_nhom20.backdend.dto.response.Adopt;

import com.webpet_nhom20.backdend.entity.Pets;
import com.webpet_nhom20.backdend.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptResponse {
    int id;
    private int userId;
    private int petId;
    private int addressId;
    private String status;
    private String note;
    private String job;
    private String income;
    private String liveCondition;
    private String isOwnPet;
    private String code;
    private String isDeleted ;
    Date createdDate;
    Date updatedDate;

}
