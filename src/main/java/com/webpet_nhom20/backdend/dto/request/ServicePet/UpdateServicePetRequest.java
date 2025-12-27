package com.webpet_nhom20.backdend.dto.request.ServicePet;

import com.webpet_nhom20.backdend.dto.request.BookingTime.UpdateBookingTimeRequest;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateServicePetRequest {
    
    /**
     * Tên dịch vụ (tùy chọn)
     * - Từ 1-255 ký tự nếu được cung cấp
     */
    @Size(min = 1, max = 255, message = "SERVICE_NAME_SIZE_INVALID")
    String name;
    
    /**
     * Tiêu đề dịch vụ (tùy chọn)
     * - Từ 1-500 ký tự nếu được cung cấp
     */
    @Size(min = 1, max = 500, message = "SERVICE_TITLE_SIZE_INVALID")
    String title;
    
    /**
     * Mô tả dịch vụ (tùy chọn)
     * - Tối đa 2000 ký tự
     */
    @Size(max = 2000, message = "SERVICE_DESCRIPTION_SIZE_INVALID")
    String description;
    
    /**
     * Thời gian dịch vụ (phút) (tùy chọn)
     * - Phải là số dương nếu được cung cấp
     */
    @Positive(message = "DURATION_MUST_BE_POSITIVE")
    Integer durationMinutes;
    
    /**
     * Giá dịch vụ (tùy chọn)
     * - Phải là số dương hoặc bằng 0 nếu được cung cấp
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "SERVICE_PRICE_MUST_BE_POSITIVE_OR_ZERO")
    BigDecimal price;
    
    /**
     * Trạng thái hoạt động
     * - Chỉ chấp nhận: 0 (không hoạt động) hoặc 1 (hoạt động)
     */
    @Pattern(regexp = "^[01]?$", message = "IS_ACTIVE_INVALID")
    String isActive;

    @Valid
    List<UpdateBookingTimeRequest> bookingTimeUpdates;
}
