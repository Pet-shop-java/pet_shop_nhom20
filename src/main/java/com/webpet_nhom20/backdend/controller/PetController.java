package com.webpet_nhom20.backdend.controller;

import com.webpet_nhom20.backdend.dto.request.Pet.FullPetCreationRequest;
import com.webpet_nhom20.backdend.dto.request.Pet.PetCreationRequest;
import com.webpet_nhom20.backdend.dto.request.Pet.PetUpdateRequest;
import com.webpet_nhom20.backdend.dto.request.Product.FullProductCreateRequest;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.Pet.FullPetCreateResponse;
import com.webpet_nhom20.backdend.dto.response.Pet.PetResponse;
import com.webpet_nhom20.backdend.dto.response.Product.FullProductCreateResponse;
import com.webpet_nhom20.backdend.dto.response.Product.ProductResponse;
import com.webpet_nhom20.backdend.service.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pets")
public class PetController {

    @Autowired
    private PetService petService;
    @PostMapping
    ApiResponse<PetResponse> createPet(PetCreationRequest request) {
        return ApiResponse.<PetResponse>builder()
                .success(true)
                .result(petService.createPet(request))
                .message("Create pet successfully")
                .build();
    }
    @PostMapping("/create-all")
    public ApiResponse<FullPetCreateResponse> createFullPet(
            @Valid @RequestBody FullPetCreationRequest request) {
        FullPetCreateResponse response = petService.createFullPet(request);
        return ApiResponse.<FullPetCreateResponse>builder()
                .success(true)
                .message(response.getMessage())
                .result(response)
                .build();
    }
    @GetMapping()
    public ApiResponse<Page<PetResponse>> getAllPets(Pageable pageable, @RequestParam (required = false) String animal, @RequestParam (required = false) String size,
                                                     @RequestParam (required = false) String ageGroup, @RequestParam (required = false) String isDeleted) {
        return ApiResponse.<Page<PetResponse>>builder().
                success(true)
                .message("Lấy danh sách sản phẩm thành công")
                .result(petService.getAllPets(isDeleted, animal , size, ageGroup, pageable)).build();
    }
    @GetMapping("/animalsCustomer")
    public ApiResponse<List<String>> getAnimalForCustomer() {
        return ApiResponse.<List<String>>builder().
                success(true)
                .message("Lấy danh sách loài thành công")
                .result(petService.getAnimalForCustomer())
                .build();
    }
    @GetMapping("/animalsAdmin")
    public ApiResponse<List<String>> getAnimalForAdmin() {
        return ApiResponse.<List<String>>builder().
                success(true)
                .message("Lấy danh sách loài thành công")
                .result(petService.getAnimalForAdmin())
                .build();
    }
    @PutMapping("/delete/{petId}")
    public ApiResponse<Void> deletePet(@PathVariable int petId) {
        petService.deletePet(petId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa thú cưng thành công")
                .build();
    }
    @GetMapping("/{petId}")
    public ApiResponse<PetResponse> getPetById(@PathVariable int petId) {
        return ApiResponse.<PetResponse>builder()
                .success(true)
                .message("Lấy thông tin thú cưng thành công")
                .result(petService.getPetById(petId))
                .build();
    }
    @PutMapping("/{petId}")
    public ApiResponse<PetResponse> updatePet(@PathVariable int petId, @Valid @RequestBody PetUpdateRequest request) {
        return ApiResponse.<PetResponse>builder()
                .success(true)
                .message("Cập nhật thú cưng thành công")
                .result(petService.updatePet(petId, request))
                .build();
    }
}
