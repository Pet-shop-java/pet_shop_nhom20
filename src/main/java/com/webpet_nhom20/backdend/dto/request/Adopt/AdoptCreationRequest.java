package com.webpet_nhom20.backdend.dto.request.Adopt;

import com.webpet_nhom20.backdend.entity.Pets;
import com.webpet_nhom20.backdend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptCreationRequest {
    int userId ;
    int petId ;
    int addressId;
    String note;
    String job;
    String income;
    String liveCondition;
    String isOwnPet;


}
