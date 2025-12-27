package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.Adopt.AdoptCreationRequest;
import com.webpet_nhom20.backdend.dto.response.Adopt.AdoptDetailResponse;
import com.webpet_nhom20.backdend.dto.response.Adopt.AdoptResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdoptService {
    AdoptResponse createAdopt(AdoptCreationRequest request);
    Page<AdoptResponse> getAdoptsByUser(int userId, String code, String status, String isDeleted, Pageable pageable);
    AdoptDetailResponse getAdoptDetail(Integer adoptId);
    void CancelAdopt(int adoptId);
    Page<AdoptResponse> getAllAdopts(Integer petId, String code, String status, String isDeleted, Pageable pageable);
    AdoptResponse updateStatusAdopt(int adoptId, String status);
}
