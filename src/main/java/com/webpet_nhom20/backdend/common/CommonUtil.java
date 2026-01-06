package com.webpet_nhom20.backdend.common;

import com.webpet_nhom20.backdend.entity.ServiceAppointments;
import com.webpet_nhom20.backdend.enums.AdoptStatus;
import com.webpet_nhom20.backdend.repository.projection.AdoptDetailProjection;

import java.time.format.DateTimeFormatter;

public class CommonUtil {

	// Mail đặt lịch
	public static String buildAppointmentEmailSubject(ServiceAppointments appointment, String userFullName,
			String userPhone) {
		return "Xác nhận đặt lịch dịch vụ chăm sóc thú cưng cho anh/chị " + userFullName + " - SĐT: "
				+ (userPhone == null ? "(Không có)" : userPhone);
	}

	public static String buildAppointmentEmailHtml(ServiceAppointments appointment, String userFullName,
			String userPhone, String serviceName) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
		String start = appointment.getAppointmentStart().format(formatter);
		String end = appointment.getAppointmentEnd().format(formatter);
		String petName = appointment.getNamePet() == null ? "(Không có)" : appointment.getNamePet();
		String speciePet = appointment.getSpeciePet() == null ? "(Không có)" : appointment.getSpeciePet();
		String notes = appointment.getNotes() == null ? "(Không có)" : appointment.getNotes();
		String safePhone = userPhone == null ? "(Không có)" : userPhone;

		String shopName = "Pet Shop";
		String supportPhone = "+84 912 345 678";
		String supportEmail = "support@petshop.vn";
		String address = "123 Đường ABC, Thường Tín, TP.Hà Nội";
		String logoUrl = "https://i.imgur.com/9z8ZQWl.png";

		return "<div style=\"font-family:Arial,sans-serif;font-size:14px;color:#1f2937\">" +
				"<div style=\"display:flex;align-items:center;gap:12px;margin-bottom:12px\">" +
				"<h2 style=\"color:#ffc107;margin:0\">" + shopName + "</h2>" +
				"</div>" +
				"<h3 style=\"color:#111827;margin:0 0 12px\">Xác nhận đặt lịch dịch vụ</h3>" +
				"<p>Chào <strong>" + userFullName + "</strong>,</p>" +
				"<p>Bạn đã đặt lịch thành công tại <strong>Pet Shop</strong>. Thông tin chi tiết:</p>" +
				"<table style=\"border-collapse:collapse;width:100%;max-width:560px\">" +
				"<tbody>" +
				row("Dịch vụ", serviceName) +
				row("Họ và tên", userFullName) +
				row("Số điện thoại", safePhone) +
				row("Tên loài", speciePet) +
				row("Tên thú cưng", petName) +
				row("Bắt đầu", start) +
				row("Kết thúc", end) +
				row("Ghi chú", notes) +
				"</tbody></table>" +
				"<p style=\"margin-top:16px\">Nếu cần thay đổi lịch hẹn, vui lòng phản hồi email này hoặc liên hệ chúng tôi.</p>"
				+
				"<div style=\"margin-top:20px;padding-top:12px;border-top:1px solid #e5e7eb;color:#6b7280;font-size:13px\">"
				+
				"<p style=\"margin:0\"><strong>" + shopName + "</strong></p>" +
				"<p style=\"margin:0\">" + address + "</p>" +
				"<p style=\"margin:0\">Hotline: " + supportPhone + " · Email: " + supportEmail + "</p>" +
				"</div>" +
				"</div>";
	}

	private static String row(String label, String value) {
		return "<tr>" +
				"<td style=\"padding:8px 12px;border:1px solid #e5e7eb;background:#f9fafb;width:30%\"><strong>" + label
				+ "</strong></td>" +
				"<td style=\"padding:8px 12px;border:1px solid #e5e7eb\">" + escapeHtml(value) + "</td>" +
				"</tr>";
	}

	private static String escapeHtml(String input) {
		if (input == null)
			return "";
		return input
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	// Mail đăng ký
	public static String buildOtpEmailSubject(String purpose) {
		return "Mã xác thực OTP - " + purpose;
	}

	public static String buildOtpEmailHtml(
			String fullName,
			String otp,
			int expireMinutes,
			String purpose) {

		String shopName = "Pet Shop";
		String supportPhone = "+84 912 345 678";
		String supportEmail = "support@petshop.vn";
		String address = "123 Đường ABC, Thường Tín, TP.Hà Nội";
		String logoUrl = "https://i.imgur.com/9z8ZQWl.png";

		String safeName = fullName == null ? "Quý khách" : escapeHtml(fullName);

		return "<div style=\"font-family:Arial,sans-serif;font-size:14px;color:#1f2937\">" +

				"<div style=\"display:flex;align-items:center;gap:12px;margin-bottom:12px\">" +
				"<h2 style=\"color:#ffc107;margin:0\">" + shopName + "</h2>" +
				"</div>" +

				"<h3 style=\"color:#111827;margin:0 0 12px\">Mã xác thực OTP</h3>" +

				"<p>Chào <strong>" + safeName + "</strong>,</p>" +

				"<p>Bạn vừa yêu cầu <strong>" + escapeHtml(purpose) + "</strong> tại <strong>" + shopName
				+ "</strong>.</p>" +

				"<p>Mã xác thực của bạn là:</p>" +

				"<div style=\"margin:16px 0;text-align:center\">" +
				"<span style=\"" +
				"display:inline-block;" +
				"padding:12px 24px;" +
				"font-size:24px;" +
				"letter-spacing:4px;" +
				"font-weight:bold;" +
				"color:#111827;" +
				"background:#f3f4f6;" +
				"border:1px dashed #d1d5db;" +
				"border-radius:8px" +
				"\">" + escapeHtml(otp) + "</span>" +
				"</div>" +

				"<p>Mã OTP có hiệu lực trong <strong>" + expireMinutes + " phút</strong>.</p>" +

				"<p style=\"color:#dc2626\"><strong>Lưu ý:</strong> Không chia sẻ mã này cho bất kỳ ai.</p>" +

				"<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>" +

				"<div style=\"margin-top:20px;padding-top:12px;border-top:1px solid #e5e7eb;color:#6b7280;font-size:13px\">"
				+
				"<p style=\"margin:0\"><strong>" + shopName + "</strong></p>" +
				"<p style=\"margin:0\">" + address + "</p>" +
				"<p style=\"margin:0\">Hotline: " + supportPhone + " · Email: " + supportEmail + "</p>" +
				"</div>" +

				"</div>";
	}

	// Mail quên mật khẩu
	public static String buildOtpForgotPasswordEmailSubject(String userFullName) {
		return "Mã OTP xác thực tài khoản Pet Shop cho anh/chị " + userFullName;
	}

	public static String buildOtpForgotPasswordEmailHtml(
			String userFullName,
			String otp,
			int expiredMinutes) {

		String shopName = "Pet Shop";
		String supportPhone = "+84 912 345 678";
		String supportEmail = "support@petshop.vn";
		String address = "123 Đường ABC, Thường Tín, TP. Hà Nội";

		return "<div style=\"font-family:Arial,sans-serif;font-size:14px;color:#1f2937\">" +

				"<h2 style=\"color:#ffc107;margin:0 0 12px\">" + shopName + "</h2>" +

				"<h3 style=\"color:#111827;margin:0 0 12px\">Xác thực bằng mã OTP</h3>" +

				"<p>Chào <strong>" + escapeHtml(userFullName) + "</strong>,</p>" +

				"<p>Bạn đã yêu cầu xác thực bằng mã OTP. Vui lòng sử dụng mã dưới đây:</p>" +

				"<div style=\"margin:16px 0;padding:16px;border:1px dashed #f59e0b;" +
				"text-align:center;font-size:24px;font-weight:bold;letter-spacing:4px;color:#92400e\">" +
				escapeHtml(otp) +
				"</div>" +

				"<table style=\"border-collapse:collapse;width:100%;max-width:560px\">" +
				"<tbody>" +
				row("Thời hạn OTP", expiredMinutes + " phút") +
				"</tbody>" +
				"</table>" +

				"<p style=\"margin-top:16px\">" +
				"Vui lòng <strong>không chia sẻ mã này cho bất kỳ ai</strong>. " +
				"Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email." +
				"</p>" +

				"<div style=\"margin-top:20px;padding-top:12px;border-top:1px solid #e5e7eb;color:#6b7280;font-size:13px\">"
				+
				"<p style=\"margin:0\"><strong>" + shopName + "</strong></p>" +
				"<p style=\"margin:0\">" + address + "</p>" +
				"<p style=\"margin:0\">Hotline: " + supportPhone + " · Email: " + supportEmail + "</p>" +
				"</div>" +

				"</div>";
	}

	// Mail nhận nuôi
	public static String buildAdoptEmailSubject(String status, String code) {

		if (AdoptStatus.COMPLETED.name().equals(status)) {
			return "Chúc mừng! Đơn nhận nuôi " + code + " đã được chấp nhận";
		}

		if (AdoptStatus.REJECTED.name().equals(status)) {
			return "Đơn nhận nuôi " + code + " đã bị từ chối";
		}

		return "Thông báo đơn nhận nuôi " + code;
	}

	public static String buildAdoptEmailHtml(AdoptDetailProjection a) {
		String fullName = a.getFullName() == null ? "Quy khach" : escapeHtml(a.getFullName());
		String shopName = "Pet Shop";
		String supportPhone = "+84 912 345 678";
		String supportEmail = "support@petshop.vn";
		String addressShop = "123 Duong ABC, Thuong Tin, TP.Ha Noi";

		return "<div style='font-family:Arial,sans-serif;font-size:14px;color:#333333;max-width:600px;margin:0 auto'>" +
				"<h2 style='color:#f59e0b;margin-bottom:8px'>" + shopName + "</h2>" +
				"<h3 style='color:#333333'>Thong bao don nhan nuoi</h3>" +
				"<p>Chao <strong>" + fullName + "</strong>,</p>" +
				"<p>Don nhan nuoi thu cung cua ban da duoc cap nhat voi thong tin sau:</p>" +

				// Thong tin don
				"<div style='background:#f9fafb;padding:12px;border-radius:8px;border:1px solid #e5e7eb;margin:16px 0'>"
				+
				"<p style='margin:4px 0'><strong>Ma don:</strong> " + escapeHtml(a.getCode()) + "</p>" +
				"<p style='margin:4px 0'><strong>Trang thai:</strong> " + escapeHtml(a.getStatus()) + "</p>" +
				"<p style='margin:4px 0'><strong>Ngay dang ky:</strong> " + a.getCreatedDate() + "</p>" +
				"</div>" +

				// Thong tin thu cung
				"<h4 style='margin-top:16px;color:#333333'>Thong tin thu cung</h4>" +
				"<table style='border-collapse:collapse;width:100%'>" +
				"<tbody>" +
				row("Ten", a.getPetName()) +
				row("Loai", a.getAnimal()) +
				row("Giong", a.getBreed()) +
				row("Tuoi", a.getAge() + " thang") +
				row("Can nang", a.getWeight() + " kg") +
				row("Gioi tinh", a.getGender()) +
				"</tbody>" +
				"</table>" +

				// Thong tin nguoi nhan nuoi
				"<h4 style='margin-top:16px;color:#333333'>Thong tin nguoi nhan nuoi</h4>" +
				"<table style='border-collapse:collapse;width:100%'>" +
				"<tbody>" +
				row("Ho ten", a.getFullName()) +
				row("SDT", a.getPhone()) +
				row("Dia chi", a.getAddress()) +
				row("Nghe nghiep", a.getJob()) +
				row("Thu nhap", a.getIncome()) +
				row("Da tung nuoi thu cung", "1".equals(a.getIsOwnPet()) ? "Co" : "Chua") +
				row("Dieu kien song", a.getLiveCondition()) +
				(a.getNote() != null ? row("Ghi chu", a.getNote()) : "") +
				"</tbody>" +
				"</table>" +

				"<p style='margin-top:16px'>Neu ban co bat ky thac mac nao, vui long lien he voi chung toi.</p>" +

				// Footer
				"<div style='margin-top:20px;padding-top:12px;border-top:1px solid #e5e7eb;color:#6b7280;font-size:13px'>"
				+
				"<p style='margin:0'><strong>" + shopName + "</strong></p>" +
				"<p style='margin:0'>" + addressShop + "</p>" +
				"<p style='margin:0'>Hotline: " + supportPhone + " - Email: " + supportEmail + "</p>" +
				"</div>" +
				"</div>";
	}

}
