package com.webpet_nhom20.backdend.dto.request.ServiceAppointment;

import com.webpet_nhom20.backdend.enums.AppoinmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateServiceAppointmentRequest {

    /**
     * ID lịch hẹn cần cập nhật
     * - Không được để trống
     */
    @NotNull(message = "ID_NOT_NULL")
    private Integer id;

    /**
     * ID dịch vụ thú cưng
     * - Không được để trống
     */
    @NotNull(message = "SERVICE_ID_NOT_NULL")
    private Integer serviceId;

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
     */
    @NotBlank(message = "SPECIE_PET_NOT_BLANK")
    @Size(max = 100, message = "SPECIE_PET_TOO_LONG")
    private String speciePet;

    /**
     * Thời gian bắt đầu lịch hẹn
     * - Không được để trống
     * - Phải là thời gian trong tương lai
     */
    @NotNull(message = "APPOINTMENT_START_NOT_NULL")
    @Future(message = "APPOINTMENT_START_NOT_FUTURE")
    private LocalDateTime appoinmentStart;

    /**
     * Trạng thái lịch hẹn (tùy chọn)
     */
    private AppoinmentStatus status;

    /**
     * Ghi chú thêm (tùy chọn)
     * - Tối đa 500 ký tự
     */
    @Size(max = 500, message = "NOTES_TOO_LONG")
    private String notes;
}
