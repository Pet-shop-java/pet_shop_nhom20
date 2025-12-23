package com.webpet_nhom20.backdend.dto.request.ServiceAppointment;

import com.webpet_nhom20.backdend.enums.AppoinmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceAppointmentsRequest {
    
    /**
     * ID dịch vụ thú cưng được đặt
     * - Không được để trống
     */
    @NotNull(message = "BOOKING_TIME_ID_NOT_NULL")
    private Integer bookingTimeId;

    /**
     * ID người dùng đặt lịch
     * - Không được để trống
     */
    @NotNull(message = "USER_ID_NOT_NULL")
    private int userId;

    /**
     * Tên thú cưng
     * - Không được để trống
     * - Tối đa 100 ký tự
     */
    @NotBlank(message = "NAME_PET_NOT_BLANK")
    @Size(max = 100, message = "NAME_PET_TOO_LONG")
    private String namePet;

    /**
     * Loài thú cưng
     * - Không được để trống
     * - Tối đa 100 ký tự
     * 
     * Ví dụ: Chó, Mèo, Thỏ
     */
    @NotBlank(message = "SPECIE_PET_NOT_BLANK")
    @Size(max = 100, message = "SPECIE_PET_TOO_LONG")
    private String speciePet;
    /**
     * Ghi chú thêm (tùy chọn)
     * - Tối đa 500 ký tự
     */
    @Size(max = 500, message = "NOTES_TOO_LONG")
    private String notes;
}
