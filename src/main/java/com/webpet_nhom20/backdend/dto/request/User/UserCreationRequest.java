package com.webpet_nhom20.backdend.dto.request.User;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    
    /**
     * Tên đăng nhập
     * - Không được để trống
     * - Tối thiểu 3 ký tự
     */
    @NotBlank(message = "USERNAME_NOT_BLANK")
    @Size(min = 3, message = "USERNAME_INVALID")
    String username;

    /**
     * Mật khẩu
     * - Không được để trống
     * - Tối thiểu 6 ký tự
     * - Phải chứa ít nhất: 1 chữ thường (a-z), 1 chữ HOA (A-Z), 
     *   1 chữ số (0-9) và 1 ký tự đặc biệt (@$!%*?&#)
     * 
     * Ví dụ hợp lệ: Password123!, MyP@ss2024, Secure#123
     */
    @NotBlank(message = "PASSWORD_NOT_BLANK")
    @Size(min = 6, message = "PASSWORD_INVALID_LENGTH")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
            message = "PASSWORD_INVALID_FORMAT"
    )
    String password;

    /**
     * Địa chỉ email
     * - Không được để trống
     * - Phải đúng định dạng email
     * 
     * Ví dụ: user@example.com
     */
    @NotBlank(message = "EMAIL_NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    String email;

    /**
     * Họ và tên đầy đủ
     * - Không được để trống
     * - Tối thiểu 3 ký tự
     */
    @NotBlank(message = "FULLNAME_NOT_BLANK")
    @Size(min = 3, message = "FULLNAME_INVALID")
    String fullName;

    /**
     * Số điện thoại
     * - Không được để trống
     * - Tối thiểu 10 ký tự
     * - Format: 0xxxxxxxxx hoặc +84xxxxxxxxx
     * - Hỗ trợ các đầu số: 03, 05, 07, 08, 09
     * 
     * Ví dụ hợp lệ: 0912345678, +84912345678, 0912 345 678
     */
    @NotBlank(message = "PHONE_NOT_BLANK")
    @Size(min = 10, message = "PHONE_INVALID")
    @Pattern(
            regexp = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$",
            message = "PHONE_FORMAT_INVALID"
    )
    String phone;
}
