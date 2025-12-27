package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.config.JwtTokenProvider;
import com.webpet_nhom20.backdend.dto.request.Adopt.AdoptCreationRequest;
import com.webpet_nhom20.backdend.dto.response.Adopt.AdoptDetailResponse;
import com.webpet_nhom20.backdend.dto.response.Adopt.AdoptResponse;
import com.webpet_nhom20.backdend.entity.Adopt;
import com.webpet_nhom20.backdend.entity.Pets;
import com.webpet_nhom20.backdend.entity.User;
import com.webpet_nhom20.backdend.enums.AdoptStatus;
import com.webpet_nhom20.backdend.enums.PetStatus;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import com.webpet_nhom20.backdend.repository.AdoptRepository;
import com.webpet_nhom20.backdend.repository.PetRepository;
import com.webpet_nhom20.backdend.repository.UserRepository;
import com.webpet_nhom20.backdend.repository.projection.AdoptDetailProjection;
import com.webpet_nhom20.backdend.service.AdoptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AdoptServiceImpl implements AdoptService {
    @Autowired
    private AdoptRepository adoptRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Override
    @Transactional
    public AdoptResponse createAdopt(AdoptCreationRequest request) {

        // 1️⃣ Check user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // 2️⃣ Check pet
        Pets pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet không tồn tại"));

        // 3️⃣ Check pet đã được nhận nuôi chưa
        if ("ADOPTED".equals(pet.getStatus())) {
            throw new RuntimeException("Pet đã được nhận nuôi");
        }

        // 4️⃣ Check đã có request PENDING chưa
        boolean exists = adoptRepository
                .existsByUserIdAndPetIdAndStatusAndIsDeletedFalse(
                        request.getUserId(),
                        request.getPetId(),
                        "PENDING"
                );

        if (exists) {
            throw new RuntimeException("Bạn đã gửi yêu cầu nhận nuôi pet này");
        }

        // 5️⃣ Tạo adopt
        Adopt adopt = Adopt.builder()
                .userId(request.getUserId())
                .petId(request.getPetId())
                .addressId(request.getAddressId())
                .note(request.getNote())
                .job(request.getJob())
                .income(request.getIncome())
                .liveCondition(request.getLiveCondition())
                .isOwnPet(request.getIsOwnPet())
                .status(AdoptStatus.PENDING.name())   // ❗ override
                .isDeleted("0")
                .build();

        adoptRepository.save(adopt);
        String code = String.format("#ADR%06d", adopt.getId());
        adopt.setCode(code);
        adoptRepository.save(adopt);

        pet.setStatus(PetStatus.PENDING_APPROVAL.name());
        petRepository.save(pet);
        return AdoptResponse.builder()
                .id(adopt.getId())
                .userId(adopt.getUserId())
                .petId(adopt.getPetId())
                .status(adopt.getStatus())
                .note(adopt.getNote())
                .job(adopt.getJob())
                .income(adopt.getIncome())
                .liveCondition(adopt.getLiveCondition())
                .isOwnPet(adopt.getIsOwnPet())
                .isDeleted(adopt.getIsDeleted())
                .createdDate(adopt.getCreatedDate())
                .updatedDate(adopt.getUpdatedDate())
                .build();
    }
    public AdoptDetailResponse getAdoptDetail(Integer adoptId) {

        AdoptDetailProjection p = adoptRepository.findAdoptDetail(adoptId)
                .orElseThrow(() -> new RuntimeException("Adopt not found"));

        return AdoptDetailResponse.builder()
                .adoptId(p.getAdoptId())
                .code(p.getCode())
                .status(p.getStatus())
                .note(p.getNote())
                .job(p.getJob())
                .income(p.getIncome())
                .isOwnPet(p.getIsOwnPet())
                .liveCondition(p.getLiveCondition())
                .createdDate(p.getCreatedDate())

                .pet(AdoptDetailResponse.Pet.builder()
                        .id(p.getPetId())
                        .name(p.getPetName())
                        .animal(p.getAnimal())
                        .breed(p.getBreed())
                        .age(p.getAge())
                        .size(p.getSize())
                        .gender(p.getGender())
                        .image(p.getPetImage())
                        .build())

                .applicant(AdoptDetailResponse.Applicant.builder()
                        .userId(p.getUserId())
                        .fullName(p.getFullName())
                        .phone(p.getPhone())
                        .address(p.getAddress())
                        .build())

                .build();
    }
    public Page<AdoptResponse> getAdoptsByUser(int userId, String code, String status, String isDeleted, Pageable pageable) {
        Page<Adopt> page = adoptRepository.findAdoptsByUserWithFilters(userId, code, status, isDeleted, pageable);

        return page.map(a -> AdoptResponse.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .petId(a.getPetId())
                .addressId(a.getAddressId())
                .code(a.getCode())
                .status(a.getStatus())
                .note(a.getNote())
                .job(a.getJob())
                .income(a.getIncome())
                .liveCondition(a.getLiveCondition())
                .isOwnPet(a.getIsOwnPet())
                .isDeleted(a.getIsDeleted())
                .createdDate(a.getCreatedDate())
                .updatedDate(a.getUpdatedDate())
                .build()
        );
    }
    @Transactional
    @Override
    public void CancelAdopt(int adoptId){
        Adopt adopt = adoptRepository.findById(adoptId)
                .orElseThrow(() -> new AppException(ErrorCode.ADOPT_NOT_FOUND));
        adopt.setStatus(AdoptStatus.CANCELED.name());
        adoptRepository.save(adopt);

        Pets pet = petRepository.findById(adopt.getPetId())
                .orElseThrow(() -> new AppException(ErrorCode.PET_NOT_FOUND));
        recalculatePetStatus(pet.getId());
    }
    public void recalculatePetStatus(int petId) {

        int approvedCount = adoptRepository
                .countByPetIdAndStatusAndIsDeleted(petId, "APPROVED", "0");

        if (approvedCount > 0) {
            petRepository.updateStatus(petId, PetStatus.ADOPTED.name());
            return;
        }

        int pendingCount = adoptRepository
                .countByPetIdAndStatusAndIsDeleted(petId, "PENDING", "0");

        if (pendingCount > 0) {
            petRepository.updateStatus(petId, PetStatus.PENDING_APPROVAL.name());
            return;
        }

        petRepository.updateStatus(petId, PetStatus.AVAILABLE.name());
    }
    @Override
    @PreAuthorize("hasRole('SHOP')")
    public Page<AdoptResponse> getAllAdopts(Integer petId, String code, String status, String isDeleted, Pageable pageable) {
        Page<Adopt> page = adoptRepository.findAllAdoptsForAdmin(petId,code,status,isDeleted,pageable);
        return page.map(a -> AdoptResponse.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .petId(a.getPetId())
                .addressId(a.getAddressId())
                .code(a.getCode())
                .status(a.getStatus())
                .note(a.getNote())
                .job(a.getJob())
                .income(a.getIncome())
                .liveCondition(a.getLiveCondition())
                .isOwnPet(a.getIsOwnPet())
                .isDeleted(a.getIsDeleted())
                .createdDate(a.getCreatedDate())
                .updatedDate(a.getUpdatedDate())
                .build()
        );
    }
    @Override
    @Transactional
    public AdoptResponse updateStatusAdopt(int adoptId, String status) {
        Adopt adopt = adoptRepository.findById(adoptId)
                .orElseThrow(() -> new RuntimeException("Adopt không tồn tại"));

        adopt.setStatus(status);
        adoptRepository.save(adopt);
        if(status.equals(AdoptStatus.COMPLETED.name())) {
            // Hủy tất cả các yêu cầu nhận nuôi khác của pet này
            adoptRepository.cancelOtherAdopts(adopt.getPetId(), adoptId);
            Pets pet = petRepository.findById(adopt.getPetId())
                    .orElseThrow(() -> new RuntimeException("Pet không tồn tại"));
            pet.setStatus(PetStatus.ADOPTED.name());
            petRepository.save(pet);
        }

        recalculatePetStatus(adopt.getPetId());

        return AdoptResponse.builder()
                .id(adopt.getId())
                .userId(adopt.getUserId())
                .petId(adopt.getPetId())
                .addressId(adopt.getAddressId())
                .code(adopt.getCode())
                .status(adopt.getStatus())
                .note(adopt.getNote())
                .job(adopt.getJob())
                .income(adopt.getIncome())
                .liveCondition(adopt.getLiveCondition())
                .isOwnPet(adopt.getIsOwnPet())
                .isDeleted(adopt.getIsDeleted())
                .createdDate(adopt.getCreatedDate())
                .updatedDate(adopt.getUpdatedDate())
                .build();
    }

}
