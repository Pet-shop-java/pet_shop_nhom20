package com.webpet_nhom20.backdend.controller;

import com.cloudinary.Api;
import com.webpet_nhom20.backdend.dto.request.ServicePet.CheckServiceNameRequest;
import com.webpet_nhom20.backdend.dto.request.ServicePet.CreateServicePetRequest;
import com.webpet_nhom20.backdend.dto.request.ServicePet.UpdateServicePetRequest;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.dto.response.ServicePet.CheckNameResponse;
import com.webpet_nhom20.backdend.dto.response.ServicePet.ServicesPetResponse;
import com.webpet_nhom20.backdend.service.ServicesPetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServicesPetController {
    @Autowired
    private final ServicesPetService services;

    @GetMapping("/active")
    public ApiResponse<List<ServicesPetResponse>> getActiveServices(){
        List<ServicesPetResponse> servicesPetResponseList = services.getActiveServices();

        return ApiResponse.<List<ServicesPetResponse>>builder()
                .result(servicesPetResponseList)
                .success(true)
                .message("Lấy dịch vụ thành công")
                .build();
    }
    @GetMapping("/{serviceId}")
    public ApiResponse<ServicesPetResponse> getServiceById(@PathVariable int serviceId){
        return ApiResponse.<ServicesPetResponse>builder()
                .success(true)
                .message("Lấy dịch vụ thành công")
                .result(services.getServiceById(serviceId))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<ServicesPetResponse>> getAllServices(String search, Pageable pageable) {
        return ApiResponse.<Page<ServicesPetResponse>>builder()
                .success(true)
                .message("Lấy danh sách dịch vụ thành công")
                .result(services.getAllServices(search,pageable))
                .build();
    }
    @PostMapping
    public ApiResponse<ServicesPetResponse> createServicePet(@RequestBody @Valid CreateServicePetRequest request){
        return ApiResponse.<ServicesPetResponse>builder()
                .success(true)
                .message("Tạo dịch vụ thành công")
                .result(services.createServicesPet(request))
                .build();
    }
    @PutMapping("/{servicePetId}")
    public ApiResponse<ServicesPetResponse> updateServicePet(@PathVariable int  servicePetId, @RequestBody @Valid UpdateServicePetRequest request){
        return ApiResponse.<ServicesPetResponse>builder()
                .success(true)
                .message("Cập nhật dịch vụ thành công")
                .result(services.updateServicesPet(servicePetId,request))
                .build();
    }

    @PostMapping("/check-title")
    public ResponseEntity<ApiResponse<CheckNameResponse>> checkServiceTitle(
            @Valid @RequestBody CheckServiceNameRequest request
    ) {

        CheckNameResponse result =
                services.checkServiceTitle(request.getTitle());

        return ResponseEntity.ok(
                ApiResponse.<CheckNameResponse>builder()
                        .success(true)
                        .message("Check service title successfully")
                        .result(result)
                        .build()
        );
    }
}
