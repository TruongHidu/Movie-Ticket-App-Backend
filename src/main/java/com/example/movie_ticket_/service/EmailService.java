package com.example.movie_ticket_.service;

import com.example.movie_ticket_.entity.PaymentMethod;
import com.example.movie_ticket_.repository.PaymentMethodRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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


    public void sendSuccessPayment(String toEmail, String customerName, String reservationCode,
                                   String movieName, String theater, String hall,
                                   String sessionTime, String seats, String foodDrinks,
                                   String paymentMethod, String paymentTime, String totalAmount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[AlphaCinema - Thông tin vé phim] - Đặt vé trực tuyến thành công / Your online ticket purchase has been successful");

        String emailContent = buildEmailContent(customerName, reservationCode, movieName,
                                                hall, sessionTime, seats, foodDrinks, paymentMethod,
                                                paymentTime, totalAmount);

        message.setText(emailContent);
        mailSender.send(message);
    }

    private String buildEmailContent(String customerName, String reservationCode, String movieName,
                                     String hall, String sessionTime, String seats,
                                     String foodDrinks, String paymentMethod, String paymentTime,
                                     String totalAmount) {

        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        StringBuilder content = new StringBuilder();

        // Tiếng Việt
        content.append("# Xin chào ").append(customerName).append(" / Hello ").append(customerName).append("\n\n");

        content.append("Mã vé (Reservation code):\n");
        content.append(reservationCode).append("\n\n");

        content.append("Phim (Movie):\n");
        content.append(movieName).append("\n\n");

        content.append("Phòng chiếu (Hall):\n");
        content.append(hall).append("\n\n");

        content.append("Thời gian (Session):\n");
        content.append(sessionTime).append("\n\n");

        content.append("Ghế (Seat):\n");
        content.append(seats).append("\n\n");

        content.append("Đồ ăn (Food):\n");
        content.append(foodDrinks != null && !foodDrinks.isEmpty() ? foodDrinks : "").append("\n\n");

        content.append("Phương thức thanh toán (Payment method): **").append(paymentMethod).append("**\n\n");

        content.append("Thời gian thanh toán (Payment Time):\n");
        content.append(paymentTime).append("\n\n");

        content.append("Tổng tiền (Total):\n");
        content.append(totalAmount).append(" VND\n\n");

        content.append("---\n\n");
        content.append(reservationCode).append("\n\n");

        content.append("Cảm ơn bạn đã đặt vé tại AlphaCinema!\n");
        content.append("Thank you for booking with AlphaCinema!");

        return content.toString();
    }

    // Overloaded method để sử dụng trực tiếp từ Invoice entity
    public void sendSuccessPayment(String toEmail, String customerName, com.example.movie_ticket_.entity.Invoice invoice) {
        // Format thời gian chiếu
        String sessionTime = invoice.getShowDate().toString() + " " + invoice.getShowTime().toString();

        // Format payment time
        String paymentTimeStr = invoice.getPaymentTime() != null ?
                invoice.getPaymentTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "";

        // Format total amount
        String totalAmountStr = invoice.getTotalPrice() != null ?
                String.format("%.2f", invoice.getTotalPrice()) : "0.00";

        PaymentMethod paymentMethod = paymentMethodRepository.findById(invoice.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payment"));

        sendSuccessPayment(
                toEmail,
                customerName,
                invoice.getTransactionNo() != null ? invoice.getTransactionNo() : String.valueOf(invoice.getId()),
                invoice.getMovieName(),
                invoice.getRoomName(), // Assuming roomName includes theater info, you might need to adjust this
                "P" + invoice.getRoomName(), // Adjust based on your actual data structure
                sessionTime,
                invoice.getSeatCodes(),
                invoice.getFoodDrinks() != null ? invoice.getFoodDrinks() : "",
                paymentMethod.getNameMethod(), // You might want to store this in your invoice
                paymentTimeStr,
                totalAmountStr
        );
    }


}
