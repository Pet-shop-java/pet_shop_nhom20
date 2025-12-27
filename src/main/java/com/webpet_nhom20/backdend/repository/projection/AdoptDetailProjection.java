package com.webpet_nhom20.backdend.repository.projection;

import java.util.Date;
public interface AdoptDetailProjection {

    Integer getAdoptId();
    String getCode();
    String getStatus();
    String getNote();
    String getJob();
    String getIncome();
    String getIsOwnPet();
    String getLiveCondition();
    Date getCreatedDate();

    Integer getUserId();
    String getFullName();
    String getPhone();

    Integer getPetId();
    String getPetName();
    String getAnimal();
    String getBreed();
    Integer getAge();
    String getSize();
    String getGender();
    String getPetImage();

    String getAddress();
}

