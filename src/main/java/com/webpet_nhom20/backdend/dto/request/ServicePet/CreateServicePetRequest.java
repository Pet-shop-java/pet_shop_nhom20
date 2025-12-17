package com.webpet_nhom20.backdend.dto.request.ServicePet;

import com.webpet_nhom20.backdend.dto.request.BookingTime.BookingTimeRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateServicePetRequest {
    
    /**
     * Tên dịch vụ
     * - Không được để trống
     * 
     * Ví dụ: Tắm rửa cho chó, Cắt tỉa lông cho mèo, Khám sức khỏe
     */
    @NotBlank(message = "SERVICE_PET_NAME_IS_NOT_NULL")
    String name;
    
    /**
     * Tiêu đề dịch vụ
     * - Không được để trống
     */
    @NotBlank(message = "SERVICE_PET_TITLE_IS_NOT_NULL")
    String title;
    
    /**
     * Mô tả chi tiết dịch vụ (tùy chọn)
     */
    String description;
    
    /**
     * Thời gian thực hiện dịch vụ (phút)
     * - Không được để trống
     * 
     * Ví dụ: 30, 60, 90
     */
    @NotNull(message = "SERVICE_PET_DURATION_IS_NOT_NULL")
    Integer durationMinutes;
    
    /**
     * Giá dịch vụ
     * - Không được để trống
     */
    @NotNull(message = "SERVICE_PET_PRICE_IS_NOT_NULL")
    BigDecimal price;

    @NotEmpty(message = "BOOKING_TIME_IS_NOT_EMPTY")
    List<BookingTimeRequest> bookingTimes;
}
