package com.webpet_nhom20.backdend.dto.request.Pet;

import com.webpet_nhom20.backdend.dto.request.PetImage.PetImageCreationDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullPetCreationRequest {
    @NotBlank(message = "Tên thú cưng không được để trống")
    @Size(max = 100, message = "Tên thú cưng tối đa 100 ký tự")
    private String name;

    @NotBlank(message = "Loại thú cưng không được để trống")
    @Size(max = 100, message = "Loại thú cưng tối đa 100 ký tự")
    private String animal; // Dog, Cat

    @Size(max = 100, message = "Giống loài tối đa 100 ký tự")
    private String breed;

    @NotNull(message = "Tuổi không được để trống")
    @Min(value = 0, message = "Tuổi phải >= 0")
    @Max(value = 50, message = "Tuổi không hợp lệ")
    private Integer age;

    @Size(max = 100, message = "Nhóm tuổi tối đa 100 ký tự")
    private String ageGroup;

    @Size(max = 100, message = "Kích thước tối đa 100 ký tự")
    private String size;

    @Size(max = 100, message = "Giới tính tối đa 100 ký tự")
    private String gender;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    @Size(max = 100, message = "Trạng thái sức khỏe tối đa 100 ký tự")
    private String healthStatus;

    @NotNull(message = "Trạng thái tiêm phòng không được để trống")
    private String vaccinated;

    @NotNull(message = "Trạng thái triệt sản không được để trống")
    private String neutered;
    @Valid // Kích hoạt validation cho các đối tượng bên trong List
    @NotEmpty(message = "Product must have at least one image")
    private List<PetImageCreationDto> images;
}
