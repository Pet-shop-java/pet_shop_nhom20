package com.webpet_nhom20.backdend.service.Impl;

import com.webpet_nhom20.backdend.dto.request.Pet.FullPetCreationRequest;
import com.webpet_nhom20.backdend.dto.request.Pet.PetCreationRequest;
import com.webpet_nhom20.backdend.dto.request.Pet.PetUpdateRequest;
import com.webpet_nhom20.backdend.dto.request.PetImage.PetImageCreationDto;
import com.webpet_nhom20.backdend.dto.request.Product.FullProductCreateRequest;
import com.webpet_nhom20.backdend.dto.request.Product_Variant.VariantCreateDto;
import com.webpet_nhom20.backdend.dto.response.Pet.FullPetCreateResponse;
import com.webpet_nhom20.backdend.dto.response.Pet.PetResponse;
import com.webpet_nhom20.backdend.dto.response.PetImage.PetImageResponse;
import com.webpet_nhom20.backdend.dto.response.Product.FullProductCreateResponse;
import com.webpet_nhom20.backdend.dto.response.Product.ProductResponse;
import com.webpet_nhom20.backdend.dto.response.ProductImage.ProductImageResponse;
import com.webpet_nhom20.backdend.entity.*;
import com.webpet_nhom20.backdend.enums.PetStatus;
import com.webpet_nhom20.backdend.exception.AppException;
import com.webpet_nhom20.backdend.repository.PetImageRepository;
import com.webpet_nhom20.backdend.repository.PetRepository;
import com.webpet_nhom20.backdend.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import com.webpet_nhom20.backdend.exception.ErrorCode;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
    @CacheEvict(value = { "pet_list", "pet_detail" }, allEntries = true)
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
                .weight(request.getWeight())
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
                .weight(savedPet.getWeight())
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
    @CacheEvict(value = { "pet_list", "pet_detail" }, allEntries = true)
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
                        .weight(request.getWeight())
                        .gender(request.getGender())
                        .description(request.getDescription())
                        .healthStatus(request.getHealthStatus())
                        .vaccinated(request.getVaccinated())
                        .neutered(request.getNeutered())
                        .isDeleted("0")
                        .status("AVAILABLE")
                        .build());

        for (int i = 0; i < request.getImages().size(); i++) {
            var imageDto = request.getImages().get(i);

            PetImages image = PetImages.builder()
                    .pet(savedPet)
                    .imageUrl(imageDto.getImageUrl())
                    .publicId(imageDto.getPublicId())
                    .imagePosition(i) // thứ tự hiển thị
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
                .weight(pet.getWeight())
                .gender(pet.getGender())
                .description(pet.getDescription())
                .healthStatus(pet.getHealthStatus())
                .vaccinated(pet.getVaccinated())
                .neutered(pet.getNeutered())
                .status(pet.getStatus())
                .isDeleted(pet.getIsDeleted())
                .createdDate(pet.getCreatedDate())
                .updatedDate(pet.getUpdatedDate())
                .build();

        List<PetImages> images = petImageRepository.findByPetIdAndIsDeleted(pet.getId(), "0");
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
    @Cacheable(value = "pet_list", key = "'p:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString() + ':' + (#isDeleted?:'') + ':' + (#animal?:'') + ':' + (#minWeight?:'') + ':' + (#maxWeight?:'') + ':' + (#ageGroup?:'') + ':' + (#breed?:'') + ':' + (#name?:'') + ':' + (#status?:'')")
    public Page<PetResponse> getAllPets(String isDeleted, String animal, Float minWeight, Float maxWeight,
            String ageGroup, String breed, String name, Pageable pageable, String status) {
        // 1️⃣ Chuẩn hóa điều kiện filter
        boolean hasAnimal = animal != null && !animal.isBlank();
        // Không cần check hasWeight nữa vì ta truyền thẳng null vào query
        boolean hasAgeGroup = ageGroup != null && !ageGroup.isBlank();
        boolean hasIsDeleted = isDeleted != null && !isDeleted.isBlank();
        boolean hasStatus = status != null && !status.isBlank();
        boolean hasBreed = breed != null && !breed.isBlank();
        boolean hasName = name != null && !name.isBlank();
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
                Sort.by(dbOrders));

        // 3️⃣ Query DB
        Page<Pets> petPage = petRepository.findAllWithFilters(
                hasAnimal ? animal : null,
                minWeight, // Truyền trực tiếp (null nếu không có)
                maxWeight, // Truyền trực tiếp (null nếu không có)
                hasAgeGroup ? ageGroup : null,
                hasBreed ? breed : null,
                hasName ? name : null,
                hasStatus ? status : null,
                hasIsDeleted ? isDeleted : null,
                dbPageable);

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
    @CacheEvict(value = { "pet_list", "pet_detail" }, allEntries = true)
    public void deletePet(int petId) {
        Pets pet = petRepository.findById(petId)
                .orElseThrow(() -> new AppException(ErrorCode.PET_NOT_FOUND));

        pet.setIsDeleted("1");
        pet.setUpdatedDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        petRepository.save(pet);
    }

    @Override
    @CacheEvict(value = { "pet_list", "pet_detail" }, allEntries = true)
    public void restorePet(int petId) {
        Pets pet = petRepository.findById(petId)
                .orElseThrow(() -> new AppException(ErrorCode.PET_NOT_FOUND));
        if ("ADOPTED".equals(pet.getStatus())) {
            throw new AppException(ErrorCode.CANNOT_RESTORE_PET_ADOPTED);
        }
        pet.setIsDeleted("0");
        pet.setUpdatedDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        petRepository.save(pet);
    }

    @Override
    @Cacheable(value = "pet_detail", key = "#petId")
    public PetResponse getPetById(int petId) {
        Pets pet = petRepository.findById(petId)
                .orElseThrow(() -> new AppException(ErrorCode.PET_NOT_FOUND));
        return mapToPesResponse(pet);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('SHOP')")
    @CacheEvict(value = { "pet_list", "pet_detail" }, allEntries = true)
    public PetResponse updatePet(int petId, PetUpdateRequest request) {

        Pets pet = petRepository.findById(petId)
                .orElseThrow(() -> new AppException(ErrorCode.PET_NOT_FOUND));

        // =========================
        // 1️⃣ UPDATE PET INFO
        // =========================
        pet.setName(request.getName());
        pet.setAnimal(request.getAnimal());
        pet.setBreed(request.getBreed());
        pet.setAge(request.getAge());
        pet.setAgeGroup(request.getAgeGroup());
        pet.setWeight(request.getWeight());
        pet.setGender(request.getGender());
        pet.setDescription(request.getDescription());
        pet.setHealthStatus(request.getHealthStatus());
        pet.setVaccinated(request.getVaccinated());
        pet.setNeutered(request.getNeutered());
        pet.setIsDeleted(request.getIsDeleted());

        petRepository.save(pet);

        // =========================
        // 2️⃣ XOÁ ẢNH CŨ (SOFT DELETE)
        // =========================
        if (request.getDeletedImageIds() != null
                && !request.getDeletedImageIds().isEmpty()) {

            petImageRepository.softDeleteImages(request.getDeletedImageIds());
        }

        // =========================
        // 3️⃣ RESET PRIMARY
        // =========================
        List<PetImages> existingImages = petImageRepository.findByPetIdAndIsDeleted(petId, "0");

        for (PetImages img : existingImages) {
            img.setIsPrimary(0);
        }
        petImageRepository.saveAll(existingImages);

        // =========================
        // 3.5️⃣ SET PRIMARY CHO ẢNH ĐÃ CÓ (NẾU CÓ)
        // =========================
        if (request.getPrimaryImageId() != null) {
            for (PetImages img : existingImages) {
                if (img.getId() == request.getPrimaryImageId().intValue()) {
                    img.setIsPrimary(1);
                    petImageRepository.save(img);
                    break;
                }
            }
        }

        // =========================
        // 4️⃣ THÊM ẢNH MỚI
        // =========================
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (PetImageCreationDto dto : request.getImages()) {
                PetImages image = PetImages.builder()
                        .pet(pet)
                        .imageUrl(dto.getImageUrl())
                        .publicId(dto.getPublicId())
                        .imagePosition(dto.getImagePosition())
                        .isPrimary(dto.isPrimary() ? 1 : 0)
                        .isDeleted("0")
                        .build();

                petImageRepository.save(image);
            }
        }

        return mapToPesResponse(pet);
    }

    @Override
    public List<String> getBreed() {
        return petRepository.getBreed();
    }

}
