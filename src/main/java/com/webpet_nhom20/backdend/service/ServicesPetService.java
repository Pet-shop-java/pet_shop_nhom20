package com.webpet_nhom20.backdend.service;

import com.webpet_nhom20.backdend.dto.request.ServicePet.CreateServicePetRequest;
import com.webpet_nhom20.backdend.dto.request.ServicePet.UpdateServicePetRequest;
import com.webpet_nhom20.backdend.dto.response.ServicePet.CheckNameResponse;
import com.webpet_nhom20.backdend.dto.response.ServicePet.ServicesPetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ServicesPetService {
    public List<ServicesPetResponse> getActiveServices();
    public ServicesPetResponse createServicesPet(CreateServicePetRequest request);
    public ServicesPetResponse updateServicesPet(int servicePetId , UpdateServicePetRequest request);
    public Page<ServicesPetResponse> getAllServices(String search, Pageable pageable);
    public ServicesPetResponse getServiceById(int serviceId);
    CheckNameResponse checkServiceTitle(String title);
}
