package com.example.movie_ticket_.service;

import com.example.movie_ticket_.entity.Invoice;
import com.example.movie_ticket_.entity.PaymentMethod;
import com.example.movie_ticket_.repository.PaymentMethodRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final PaymentMethodRepository paymentMethodRepository;



    public void sendRandomPassword(String toEmail, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mật khẩu đăng nhập lần đầu");
        message.setText("Chào bạn, mật khẩu đăng nhập lần đầu của bạn là: " + password +
                                "\nVui lòng đổi mật khẩu sau khi đăng nhập.");
        mailSender.send(message);
    }


    public void sendSuccessPayment(String toEmail, String customerName, Invoice invoice) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            PaymentMethod paymentMethod = paymentMethodRepository.findById(invoice.getPaymentMethodId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy payment"));

            String sessionTime = invoice.getShowDate() + " " + invoice.getShowTime();
            String paymentTimeStr = invoice.getPaymentTime() != null
                    ? invoice.getPaymentTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    : "";
            String totalAmountStr = invoice.getTotalPrice() != null
                    ? String.format("%,.0f VND", invoice.getTotalPrice())
                    : "0 VND";

            // ✅ Thêm dòng ảnh barcode
            String barcodeImageHtml = "";
            if (invoice.getBarcodeUrl() != null && !invoice.getBarcodeUrl().isEmpty()) {
                barcodeImageHtml = """
                    <div style="text-align:center; margin-top:20px;">
                        <p style="font-weight:bold; color:#333;">Mã vạch (Barcode)</p>
                        <img src="%s" alt="Barcode" style="max-width:250px; height:auto; border:1px solid #ddd; padding:5px; border-radius:6px;">
                    </div>
                    """.formatted(invoice.getBarcodeUrl());
            }

            String emailContent = """
                    <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #222;">
                        <h2 style="text-align:center; color:#b21f2d;">AlphaCinema - Xác nhận đặt vé thành công</h2>
                        <p>Xin chào <b>%s</b>,</p>
                        <p>Cảm ơn bạn đã đặt vé tại <b>AlphaCinema</b>!</p>
                        <table style="width:100%%; border-collapse:collapse; font-size:14px;">
                            <tr><td style="width:220px; font-weight:bold;">Mã vé (Reservation Code):</td><td>%s</td></tr>
                            <tr><td style="font-weight:bold;">Phim (Movie):</td><td>%s</td></tr>
                            <tr><td style="font-weight:bold;">Phòng chiếu (Hall):</td><td>%s</td></tr>
                            <tr><td style="font-weight:bold;">Thời gian (Session):</td><td>%s</td></tr>
                            <tr><td style="font-weight:bold;">Ghế (Seats):</td><td>%s</td></tr>
                            %s
                            <tr><td style="font-weight:bold;">Phương thức thanh toán:</td><td>%s</td></tr>
                            <tr><td style="font-weight:bold;">Thời gian thanh toán:</td><td>%s</td></tr>
                            <tr><td style="font-weight:bold;">Tổng tiền:</td><td><b style="color:#d32f2f;">%s</b></td></tr>
                        </table>
                        %s
                        <br>
                        <p style="text-align:center;">Chúc bạn xem phim vui vẻ! 🍿</p>
                        <p style="text-align:center; font-size:13px; color:gray;">AlphaCinema Team</p>
                    </div>
                    """.formatted(
                    customerName,
                    invoice.getTransactionNo() != null ? invoice.getTransactionNo() : String.valueOf(invoice.getId()),
                    invoice.getMovieName(),
                    invoice.getRoomName(),
                    sessionTime,
                    invoice.getSeatCodes(),
                    (invoice.getFoodDrinks() != null && !invoice.getFoodDrinks().isEmpty())
                            ? "<tr><td style='font-weight:bold;'>Combo (Food):</td><td>" + invoice.getFoodDrinks() + "</td></tr>"
                            : "",
                    paymentMethod.getNameMethod(),
                    paymentTimeStr,
                    totalAmountStr,
                    barcodeImageHtml // ✅ hiển thị ảnh barcode
            );

            helper.setTo(toEmail);
            helper.setSubject("[AlphaCinema] - Xác nhận đặt vé thành công");
            helper.setText(emailContent, true); // true = HTML
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại", e);
        }
    }



}
