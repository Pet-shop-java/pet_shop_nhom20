package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.dto.request.Pet.FullPetCreationRequest;
import com.webpet_nhom20.backdend.dto.request.Pet.PetCreationRequest;
import com.webpet_nhom20.backdend.dto.request.Pet.PetUpdateRequest;
import com.webpet_nhom20.backdend.dto.request.Product.FullProductCreateRequest;
import com.webpet_nhom20.backdend.dto.request.Product_Variant.VariantCreateDto;
import com.webpet_nhom20.backdend.dto.response.Pet.FullPetCreateResponse;
import com.webpet_nhom20.backdend.dto.response.Pet.PetResponse;
import com.webpet_nhom20.backdend.dto.response.PetImage.PetImageResponse;
import com.webpet_nhom20.backdend.dto.response.Product.FullProductCreateResponse;
import com.webpet_nhom20.backdend.dto.response.Product.ProductResponse;
import com.webpet_nhom20.backdend.dto.response.ProductImage.ProductImageResponse;
import com.webpet_nhom20.backdend.entity.*;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.repository.PetImageRepository;
import com.webpet_nhom20.backdend.repository.PetRepository;
import com.webpet_nhom20.backdend.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl implements PetService {
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PetImageRepository petImageRepository;

    @PreAuthorize("hasRole('SHOP')")
    @Transactional
    @Override
    public PetResponse createPet(PetCreationRequest request) {

        if (petRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PET_IS_EXISTED);
        }

        Pets pet = Pets.builder()
                .name(request.getName())
                .animal(request.getAnimal())
                .breed(request.getBreed())
                .age(request.getAge())
                .ageGroup(request.getAgeGroup())
                .size(request.getSize())
                .gender(request.getGender())
                .description(request.getDescription())
                .healthStatus(request.getHealthStatus())
                .vaccinated(request.getVaccinated())
                .neutered(request.getNeutered())
                .isDeleted("0")
                .build();

        Pets savedPet = petRepository.save(pet);

        return PetResponse.builder()
                .id(savedPet.getId())
                .name(savedPet.getName())
                .animal(savedPet.getAnimal())
                .breed(savedPet.getBreed())
                .age(savedPet.getAge())
                .ageGroup(savedPet.getAgeGroup())
                .size(savedPet.getSize())
                .gender(savedPet.getGender())
                .description(savedPet.getDescription())
                .healthStatus(savedPet.getHealthStatus())
                .vaccinated(savedPet.getVaccinated())
                .neutered(savedPet.getNeutered())
                .createdDate(savedPet.getCreatedDate())
                .build();
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('SHOP')")
    public FullPetCreateResponse createFullPet(FullPetCreationRequest request) {

        if (petRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PET_IS_EXISTED);
        }

        if (request.getImages() == null || request.getImages().isEmpty()) {
            throw new AppException(ErrorCode.PET_MUST_HAVE_IMAGE);
        }

        Pets savedPet = petRepository.save(
                Pets.builder()
                        .name(request.getName())
                        .animal(request.getAnimal())
                        .breed(request.getBreed())
                        .age(request.getAge())
                        .ageGroup(request.getAgeGroup())
                        .size(request.getSize())
                        .gender(request.getGender())
                        .description(request.getDescription())
                        .healthStatus(request.getHealthStatus())
                        .vaccinated(request.getVaccinated())
                        .neutered(request.getNeutered())
                        .isDeleted("0")
                        .build()
        );

        for (int i = 0; i < request.getImages().size(); i++) {
            var imageDto = request.getImages().get(i);

            PetImages image = PetImages.builder()
                    .pet(savedPet)
                    .imageUrl(imageDto.getImageUrl())
                    .publicId(imageDto.getPublicId())
                    .imagePosition(i)          // thứ tự hiển thị
                    .isPrimary(i == 0 ? 1 : 0) // ảnh đầu tiên là primary
                    .isDeleted("0")
                    .build();

            petImageRepository.save(image);
        }

        return FullPetCreateResponse.builder()
                .petId(savedPet.getId())
                .message("Tạo thú cưng thành công")
                .build();
    }
    private PetResponse mapToPesResponse(Pets pet) {
        PetResponse response = PetResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .animal(pet.getAnimal())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .ageGroup(pet.getAgeGroup())
                .size(pet.getSize())
                .gender(pet.getGender())
                .description(pet.getDescription())
                .healthStatus(pet.getHealthStatus())
                .vaccinated(pet.getVaccinated())
                .neutered(pet.getNeutered())
                .status(pet.getStatus())
                .isDeleted("0")
                .createdDate(pet.getCreatedDate())
                .updatedDate(pet.getUpdatedDate())
                .build();

        List<PetImages> images = petImageRepository.findByPetId(pet.getId());
        List<PetImageResponse> imageResponses = images.stream()
                .map(image -> PetImageResponse.builder()
                        .id(image.getId())
                        .petId(image.getPet().getId())
                        .imageUrl(image.getImageUrl())
                        .position(image.getImagePosition())
                        .isPrimary(image.getIsPrimary())
                        .isDeleted(image.getIsDeleted())
                        .createdDate(image.getCreatedDate())
                        .updatedDate(image.getUpdatedDate())
                        .build())
                .collect(Collectors.toList());
        response.setImages(imageResponses);
        return response;
    }
    @Override
    public Page<PetResponse> getAllPets(String isDeleted, String animal, String size, String ageGroup, Pageable pageable, String status) {
        // 1️⃣ Chuẩn hóa điều kiện filter
        boolean hasAnimal = animal != null && !animal.isBlank();
        boolean hasSize = size != null && !size.isBlank();
        boolean hasAgeGroup = ageGroup != null && !ageGroup.isBlank();
        boolean hasIsDeleted = isDeleted != null && !isDeleted.isBlank();
        boolean hasStatus = status != null && !status.isBlank();
        // 2️⃣ Mapping SORT (camelCase → snake_case)
        List<Sort.Order> dbOrders = pageable.getSort().stream()
                .map(order -> {
                    String property = order.getProperty();
                    if ("createdDate".equals(property))
                        return new Sort.Order(order.getDirection(), "created_date");
                    if ("updatedDate".equals(property))
                        return new Sort.Order(order.getDirection(), "updated_date");
                    return order;
                })
                .toList();

        Pageable dbPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(dbOrders)
        );

        // 3️⃣ Query DB
        Page<Pets> petPage = petRepository.findAllWithFilters(
                hasAnimal ? animal : null,
                hasSize ? size : null,
                hasAgeGroup ? ageGroup : null,
                hasStatus ? status : null,
                hasIsDeleted ? isDeleted : null,
                dbPageable
        );

        // 4️⃣ Map Entity → Response
        List<PetResponse> responses = petPage.getContent()
                .stream()
                .map(this::mapToPesResponse)
                .toList();

        return new PageImpl<>(responses, pageable, petPage.getTotalElements());
    }
    @Override
    public List<String> getAnimalForCustomer() {
        return petRepository.getAnimalForCustomer();
    }
    @Override
    public List<String> getAnimalForAdmin() {
        return petRepository.getAnimalForAdmin();
    }
    @Override
    public void deletePet(int petId) {
        Pets pet = petRepository.findById(petId)
                .orElseThrow(() -> new AppException(ErrorCode.PET_NOT_FOUND));

        pet.setIsDeleted("1");
        pet.setUpdatedDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        petRepository.save(pet);
    }
    @Override
    public PetResponse getPetById(int petId) {
        Pets pet = petRepository.findById(petId)
                .orElseThrow(() -> new AppException(ErrorCode.PET_NOT_FOUND));
        return mapToPesResponse(pet);
    }
    @Override
    @PreAuthorize("hasRole('SHOP')")
    @Transactional
    public PetResponse updatePet(int petId, PetUpdateRequest request) {
        Pets pet = petRepository.findById(petId)
                .orElseThrow(() -> new AppException(ErrorCode.PET_NOT_FOUND));
        pet.setName(request.getName());
        pet.setAnimal(request.getAnimal());
        pet.setBreed(request.getBreed());
        pet.setAge(request.getAge());
        pet.setAgeGroup(request.getAgeGroup());
        pet.setSize(request.getSize());
        pet.setGender(request.getGender());
        pet.setDescription(request.getDescription());
        pet.setHealthStatus(request.getHealthStatus());
        pet.setVaccinated(request.getVaccinated());
        pet.setNeutered(request.getNeutered());
        pet.setUpdatedDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        Pets updatedPet = petRepository.save(pet);
        return mapToPesResponse(updatedPet);
    }
}
