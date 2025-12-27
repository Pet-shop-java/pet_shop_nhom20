package com.webpet_nhom20.backdend.controller;

import com.webpet_nhom20.backdend.dto.request.Adopt.AdoptCreationRequest;
import com.webpet_nhom20.backdend.dto.response.Adopt.AdoptDetailResponse;
import com.webpet_nhom20.backdend.dto.response.Adopt.AdoptResponse;
import com.webpet_nhom20.backdend.dto.response.ApiResponse;
import com.webpet_nhom20.backdend.service.AdoptService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/adopt")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class AdoptController {
    @Autowired
    private AdoptService adoptService;
    @PostMapping
    ApiResponse<AdoptResponse> createAdopt(@RequestBody @Valid AdoptCreationRequest request){
        return ApiResponse.<AdoptResponse>builder()
                .success(true)
                .result(adoptService.createAdopt(request))
                .message("Adopt created successfully")
                .build();
    }
    @GetMapping("/user/{userId}")
    public ApiResponse<Page<AdoptResponse>> getAdoptByUserId(@PathVariable int userId, @RequestParam(required = false) String code, @RequestParam(required = false) String status,
            @RequestParam(required = false) String isDeleted, Pageable pageable) {
        return ApiResponse.<Page<AdoptResponse>>builder()
                .success(true)
                .result(adoptService.getAdoptsByUser(userId, code, status, isDeleted, pageable))
                .message("Get adopts by user successfully")
                .build();
    }
    @GetMapping("/{adoptId}")
    ApiResponse<AdoptDetailResponse> getAdoptDetail(@PathVariable int adoptId) {
        return ApiResponse.<AdoptDetailResponse>builder()
                .success(true)
                .result(adoptService.getAdoptDetail(adoptId))
                .message("Get adopt by id successfully")
                .build();
    }
    @PutMapping("/cancel/{adoptId}")
    ApiResponse<Void> cancelAdopt(@PathVariable int adoptId) {
        adoptService.CancelAdopt(adoptId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Cancel adopt successfully")
                .build();
    }
    @GetMapping("/all")
    public ApiResponse<Page<AdoptResponse>> getAllAdopts(@RequestParam(required = false) Integer petId,
                                                          @RequestParam(required = false) String code,
                                                          @RequestParam(required = false) String status,
                                                          @RequestParam(required = false) String isDeleted,
                                                          @PageableDefault(size = 10, sort = "created_date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.<Page<AdoptResponse>>builder()
                .success(true)
                .result(adoptService.getAllAdopts(petId, code, status, isDeleted, pageable))
                .message("Get all adopts successfully")
                .build();
    }
    @PutMapping("/status/{adoptId}")
    ApiResponse<AdoptResponse> updateStatusAdopt(@PathVariable int adoptId, @RequestParam String status) {
        return ApiResponse.<AdoptResponse>builder()
                .success(true)
                .result(adoptService.updateStatusAdopt(adoptId, status))
                .message("Update adopt status successfully")
                .build();
    }
}
