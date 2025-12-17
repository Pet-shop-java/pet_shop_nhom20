package com.webpet_nhom20.backdend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
public enum ErrorCode {
    // ===== LỖI HỆ THỐNG (9xxx) =====
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(9001, "Khóa thông báo không hợp lệ", HttpStatus.BAD_REQUEST),

    // ===== LỖI CHUNG (1000-1099) =====
    SUCCESS(1000, "Thành công", HttpStatus.OK),
    ID_NOT_NULL(1001, "ID không được để trống", HttpStatus.BAD_REQUEST),
    IS_DELETED_VALID(1002, "Giá trị isDeleted phải là 0 hoặc 1", HttpStatus.BAD_REQUEST),
    IS_FEATURED_VALID(1003, "Giá trị isFeatured phải là 0 hoặc 1", HttpStatus.BAD_REQUEST),

    // ===== LỖI XÁC THỰC & PHÂN QUYỀN (1100-1199) =====
    UNAUTHENTICATED(1100, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1101, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    ACCESS_DENIED(1102, "Truy cập bị từ chối", HttpStatus.FORBIDDEN),
    IDENTIFIER_NOT_BLANK(1103, "Tên đăng nhập hoặc email không được để trống", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_BLANK(1104, "Token không được để trống", HttpStatus.BAD_REQUEST),
    IS_EMPTY(1105,"Danh sách rỗng",HttpStatus.BAD_REQUEST),
    // ===== LỖI NGƯỜI DÙNG (1200-1299) =====
    // Validation lỗi rỗng
    USERNAME_NOT_BLANK(1200, "Tên đăng nhập không được để trống", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_BLANK(1201, "Mật khẩu không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_BLANK(1202, "Email không được để trống", HttpStatus.BAD_REQUEST),
    FULLNAME_NOT_BLANK(1203, "Họ tên không được để trống", HttpStatus.BAD_REQUEST),
    PHONE_NOT_BLANK(1204, "Số điện thoại không được để trống", HttpStatus.BAD_REQUEST),

    // Validation định dạng
    USERNAME_INVALID(1210, "Tên đăng nhập phải có ít nhất 3 ký tự", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID_LENGTH(1211, "Mật khẩu phải có ít nhất 6 ký tự", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID_FORMAT(1212, "Mật khẩu phải chứa ít nhất: 1 chữ thường, 1 chữ hoa, 1 số và 1 ký tự đặc biệt (@$!%*?&#)", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1213, "Định dạng email không hợp lệ", HttpStatus.BAD_REQUEST),
    FULLNAME_INVALID(1214, "Họ tên phải có ít nhất 3 ký tự", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(1215, "Số điện thoại phải có ít nhất 10 ký tự", HttpStatus.BAD_REQUEST),
    PHONE_FORMAT_INVALID(1216, "Định dạng số điện thoại không hợp lệ (VD: 0xxxxxxxxx hoặc +84xxxxxxxxx)", HttpStatus.BAD_REQUEST),

    // Lỗi trùng lặp
    USER_EXISTED(1220, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1221, "Email đã được sử dụng", HttpStatus.BAD_REQUEST),
    PHONE_EXISTED(1222, "Số điện thoại đã được sử dụng", HttpStatus.BAD_REQUEST),
    PASSWORD_EXISTED(1223, "Mật khẩu đã được sử dụng", HttpStatus.BAD_REQUEST),

    // Lỗi không tìm thấy
    USER_NOT_EXISTS(1230, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    USER_DELETED(1231, "Người dùng đã bị xóa", HttpStatus.OK),

    // ===== LỖI VAI TRÒ & QUYỀN (1300-1399) =====
    ROLE_NAME_NOT_NULL(1300, "Tên vai trò không được để trống", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1301, "Vai trò không tồn tại", HttpStatus.NOT_FOUND),
    PERMISSION_NAME_NOT_BLANK(1302, "Tên quyền không được để trống", HttpStatus.BAD_REQUEST),

    // ===== LỖI DANH MỤC (1400-1499) =====
    CATEGORY_NAME_IS_NOT_NULL(1400, "Tên danh mục không được để trống", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(1401, "Danh mục không tồn tại", HttpStatus.NOT_FOUND),
    CATEGORY_IS_EXISTED(1402, "Danh mục đã tồn tại", HttpStatus.BAD_REQUEST),
    CATEGORY_ID_NOT_NULL(1403, "ID danh mục không được để trống", HttpStatus.BAD_REQUEST),

    // ===== LỖI SẢN PHẨM (1500-1599) =====
    PRODUCT_NAME_IS_NOT_NULL(1500, "Tên sản phẩm không được để trống", HttpStatus.BAD_REQUEST),
    PRODUCT_PRICE_IS_NOT_NULL(1501, "Giá sản phẩm không được để trống", HttpStatus.BAD_REQUEST),
    PRODUCT_IS_EXISTED(1502, "Sản phẩm đã tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1503, "Sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    PRODUCT_ID_NOT_NULL(1504, "ID sản phẩm không được để trống", HttpStatus.BAD_REQUEST),

    // ===== LỖI BIẾN THỂ SẢN PHẨM (1600-1699) =====
    VARIANT_NAME_IS_NOT_NULL(1600, "Tên biến thể không được để trống", HttpStatus.BAD_REQUEST),
    VARIANT_WEIGHT_IS_NOT_NULL(1601, "Khối lượng không được để trống", HttpStatus.BAD_REQUEST),
    VARIANT_PRICE_IS_NOT_NULL(1602, "Giá không được để trống", HttpStatus.BAD_REQUEST),
    VARIANT_NAME_IS_EXISTED(1603, "Tên biến thể đã tồn tại", HttpStatus.BAD_REQUEST),
    VARIANT_NOT_FOUND(1604, "Biến thể không tồn tại", HttpStatus.NOT_FOUND),

    // Validation mới cho Product Variant
    PRODUCT_ID_MUST_BE_POSITIVE(1605, "ID sản phẩm phải là số dương", HttpStatus.BAD_REQUEST),
    PRODUCT_IMAGE_ID_NOT_NULL(1606, "ID hình ảnh sản phẩm không được để trống", HttpStatus.BAD_REQUEST),
    PRODUCT_IMAGE_ID_MUST_BE_POSITIVE(1607, "ID hình ảnh sản phẩm phải là số dương", HttpStatus.BAD_REQUEST),
    VARIANT_NAME_NOT_BLANK(1608, "Tên biến thể không được để trống", HttpStatus.BAD_REQUEST),
    VARIANT_NAME_SIZE_INVALID(1609, "Tên biến thể phải từ 1-255 ký tự", HttpStatus.BAD_REQUEST),
    WEIGHT_MUST_BE_POSITIVE(1610, "Khối lượng phải là số dương", HttpStatus.BAD_REQUEST),
    VARIANT_PRICE_MUST_BE_POSITIVE_OR_ZERO(1611, "Giá phải là số dương hoặc bằng 0", HttpStatus.BAD_REQUEST),
    STOCK_QUANTITY_MUST_BE_POSITIVE_OR_ZERO(1612, "Số lượng tồn kho phải là số dương hoặc bằng 0", HttpStatus.BAD_REQUEST),
    IS_DELETED_INVALID(1613, "Giá trị isDeleted phải là 0 hoặc 1", HttpStatus.BAD_REQUEST),

    // ===== LỖI HÌNH ẢNH (1700-1799) =====
    IMAGE_NOT_FOUND(1700, "Hình ảnh không tồn tại", HttpStatus.NOT_FOUND),
    IMAGE_IS_DELETE(1701, "Hình ảnh đã bị xóa", HttpStatus.BAD_REQUEST),
    IMAGE_IS_PRIMARY(1702, "Hình ảnh đang là ảnh chính", HttpStatus.BAD_REQUEST),
    IMAGE_IS_NOT_PRIMARY_AND_DELETE(1703, "Hình ảnh không phải ảnh chính và đã bị xóa", HttpStatus.BAD_REQUEST),
    PRIMARY_IMAGE_ALREADY_EXISTS(1704, "Sản phẩm đã có ảnh chính, không thể thêm ảnh chính khác", HttpStatus.BAD_REQUEST),
    MAX_FILE_SIZE(1705, "Kích thước file tối đa là 2MB", HttpStatus.BAD_REQUEST),
    FORMAT_FILE_VALID(1706, "Chỉ chấp nhận định dạng jpg|jpeg|png|gif|bmp|webp", HttpStatus.BAD_REQUEST),
    FAIL_TO_UPLOAD_FILE(1707, "Tải lên file thất bại", HttpStatus.BAD_REQUEST),
    IMAGE_URL_NOT_BLANK(1708, "URL hình ảnh không được để trống", HttpStatus.BAD_REQUEST),
    IS_PRIMARY_VALID(1709, "Giá trị isPrimary phải là 0 hoặc 1", HttpStatus.BAD_REQUEST),

    // ===== LỖI DỊCH VỤ THÚ CƯNG (1800-1899) =====
    SERVICE_PET_NAME_IS_NOT_NULL(1800, "Tên dịch vụ không được để trống", HttpStatus.BAD_REQUEST),
    SERVICE_PET_TITLE_IS_NOT_NULL(1801, "Tiêu đề dịch vụ không được để trống", HttpStatus.BAD_REQUEST),
    SERVICE_PET_DURATION_IS_NOT_NULL(1802, "Thời gian dịch vụ không được để trống", HttpStatus.BAD_REQUEST),
    SERVICE_PET_PRICE_IS_NOT_NULL(1803, "Giá dịch vụ không được để trống", HttpStatus.BAD_REQUEST),
    SERVICE_PET_IS_EXISTED(1804, "Dịch vụ đã tồn tại", HttpStatus.BAD_REQUEST),
    SERVICE_PET_NOT_FOUND(1805, "Dịch vụ không tồn tại", HttpStatus.NOT_FOUND),
    SERVICE_NOT_FOUND(1806, "Dịch vụ không tồn tại", HttpStatus.NOT_FOUND),
    SERVICE_ID_NOT_NULL(1807, "ID dịch vụ không được để trống", HttpStatus.BAD_REQUEST),

    // Validation mới cho Service Pet
    SERVICE_NAME_SIZE_INVALID(1808, "Tên dịch vụ phải từ 1-255 ký tự", HttpStatus.BAD_REQUEST),
    SERVICE_TITLE_SIZE_INVALID(1809, "Tiêu đề dịch vụ phải từ 1-500 ký tự", HttpStatus.BAD_REQUEST),
    SERVICE_DESCRIPTION_SIZE_INVALID(1810, "Mô tả dịch vụ không được vượt quá 2000 ký tự", HttpStatus.BAD_REQUEST),
    DURATION_MUST_BE_POSITIVE(1811, "Thời gian dịch vụ phải là số dương", HttpStatus.BAD_REQUEST),
    SERVICE_PRICE_MUST_BE_POSITIVE_OR_ZERO(1812, "Giá dịch vụ phải là số dương hoặc bằng 0", HttpStatus.BAD_REQUEST),
    IS_ACTIVE_INVALID(1813, "Giá trị isActive phải là 0 hoặc 1", HttpStatus.BAD_REQUEST),

    // ===== LỖI LỊCH HẸN (1900-1999) =====
    USER_ID_NOT_NULL(1900, "ID người dùng không được để trống", HttpStatus.BAD_REQUEST),
    NAME_PET_NOT_BLANK(1901, "Tên thú cưng không được để trống", HttpStatus.BAD_REQUEST),
    NAME_PET_TOO_LONG(1902, "Tên thú cưng không được vượt quá 100 ký tự", HttpStatus.BAD_REQUEST),
    SPECIE_PET_NOT_BLANK(1903, "Loài thú cưng không được để trống", HttpStatus.BAD_REQUEST),
    SPECIE_PET_TOO_LONG(1904, "Loài thú cưng không được vượt quá 100 ký tự", HttpStatus.BAD_REQUEST),
    APPOINTMENT_START_NOT_NULL(1905, "Thời gian bắt đầu không được để trống", HttpStatus.BAD_REQUEST),
    APPOINTMENT_START_NOT_FUTURE(1906, "Thời gian bắt đầu phải là thời gian trong tương lai", HttpStatus.BAD_REQUEST),
    NOTES_TOO_LONG(1907, "Ghi chú không được vượt quá 500 ký tự", HttpStatus.BAD_REQUEST),
    APPOINTMENT_NOT_FOUND(1908, "Lịch hẹn không tồn tại", HttpStatus.NOT_FOUND),
    ALREADY_CANCELED(1909, "Lịch hẹn đã bị hủy", HttpStatus.BAD_REQUEST),
    ALREADY_COMPLETED(1910, "Lịch hẹn đã hoàn thành", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED_CANCEL(1911, "Không có quyền hủy lịch hẹn", HttpStatus.FORBIDDEN),

    //Address (2000-2099)
    ADDRESS_NOT_FOUND(2000,"Địa chỉ không tồn tại",HttpStatus.BAD_REQUEST),
    ADDRESS_IS_NOT_NULL(2001,"Địa chỉ không được để trống",HttpStatus.BAD_REQUEST),
    CONTACT_NAME_IS_NOT_NULL(2002,"Tên người liên hệ không được để trống",HttpStatus.BAD_REQUEST),
    DETAIL_ADDRESS_IS_NOT_NULL(2003,"Địa chỉ chi tiết không được để trống",HttpStatus.BAD_REQUEST),
    CITY_IS_NOT_NULL(2004,"Tỉnh/Thành phố không được để trống",HttpStatus.BAD_REQUEST),
    STATE_IS_NOT_NULL(2005,"Quận/Huyện không được để trống",HttpStatus.BAD_REQUEST),
    WARD_IS_NOT_NULL(2006,"Phường/Xã không được để trống",HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_DEFAULT_ADDRESS(2007,"Không thể xóa địa chỉ mặc định",HttpStatus.BAD_REQUEST),
    //User (2100-2199)
    UNCORRECT_PASSWORD(1223,"Mật khẩu cũ không đúng", HttpStatus.BAD_REQUEST),

    //Order (2200-2299)
    ORDER_NOT_FOUND(2200,"Đơn hàng không tồn tại",HttpStatus.BAD_REQUEST),
    ORDER_STATUS_INVALID(2201,"Trạng thái đơn hàng không hợp lệ",HttpStatus.BAD_REQUEST),
    CANNOT_CANCEL_COMPLETED_ORDER(2202,"Không thể hủy đơn hàng đã hoàn thành",HttpStatus.BAD_REQUEST),
    CANNOT_CANCEL_CANCELED_ORDER(2203,"Không thể hủy đơn hàng đã bị hủy",HttpStatus.BAD_REQUEST),
    PRODUCT_VARIANT_ID_IS_NOT_NULL(2204,"ID biến thể sản phẩm không được để trống",HttpStatus.BAD_REQUEST),
    CANNOT_CANCEL_ORDER(2205,"Không thể hủy đơn hàng này",HttpStatus.BAD_REQUEST)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
