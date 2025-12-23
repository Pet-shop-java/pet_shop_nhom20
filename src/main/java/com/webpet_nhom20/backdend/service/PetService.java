package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.Pet.FullPetCreationRequest;
import com.webpet_nhom20.backdend.dto.request.Pet.PetCreationRequest;
import com.webpet_nhom20.backdend.dto.request.Pet.PetUpdateRequest;
import com.webpet_nhom20.backdend.dto.response.Pet.FullPetCreateResponse;
import com.webpet_nhom20.backdend.dto.response.Pet.PetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PetService {
    PetResponse createPet(PetCreationRequest request);
    FullPetCreateResponse createFullPet(FullPetCreationRequest request);
    Page<PetResponse> getAllPets(String isDeleted , String animal, String size, String ageGroup, Pageable pageable);
    List<String> getAnimalForCustomer();
    List<String> getAnimalForAdmin();
    void deletePet(int petId);
    PetResponse getPetById(int petId);
    PetResponse updatePet(int petId, PetUpdateRequest request);
}
