package com.example.movie_ticket_.scheduler;

import com.example.movie_ticket_.entity.Invoice;
import com.example.movie_ticket_.entity.UserDeviceToken;
import com.example.movie_ticket_.repository.InvoiceRepository;
import com.example.movie_ticket_.repository.UserDeviceTokenRepository;
import com.example.movie_ticket_.service.FCMService;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private final InvoiceRepository invoiceRepository;
    private final UserDeviceTokenRepository deviceTokenRepo;
    private final FCMService fcmService;

    @Scheduled(fixedRate = 60000)
    public void sendUpcomingShowNotifications() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusMinutes(60);

        List<Invoice> invoices =
                invoiceRepository.findInvoicesNeedProcess(threshold);

        for (Invoice invoice : invoices) {

            LocalDateTime showDateTime =
                    LocalDateTime.of(invoice.getShowDate(), invoice.getShowTime());

            // 1️⃣ Quá giờ → chỉ cập nhật
            if (showDateTime.isBefore(now)) {
                invoice.setStatus(2);
                invoiceRepository.save(invoice);
                continue;
            }

            // 2️⃣ Lấy danh sách thiết bị
            List<UserDeviceToken> devices =
                    deviceTokenRepo.findByUser(invoice.getUser());

            // Không có thiết bị → chỉ cập nhật
            if (devices.isEmpty()) {
                invoice.setStatus(2);
                invoiceRepository.save(invoice);
                continue;
            }

            // 3️⃣ Gửi thông báo
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy");

            String title = "Sắp đến giờ xem phim!";
            String body = "Phim " + invoice.getMovieName()
                    + " chiếu lúc " + invoice.getShowTime()
                    + " " + invoice.getShowDate().format(formatter)
                    + ". Ghế: " + invoice.getSeatCodes();

            for (UserDeviceToken device : devices) {
                String fcmToken = device.getFcmToken();
                if (fcmToken == null || fcmToken.isBlank()) continue;

                try {
                    fcmService.sendNotification(fcmToken, title, body, invoice.getId());
                } catch (FirebaseMessagingException e) {
                    if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED ||
                            e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                        deviceTokenRepo.delete(device);
                    }
                }
            }

            // 4️⃣ Đánh dấu đã xử lý
            invoice.setStatus(2);
            invoiceRepository.save(invoice);
        }
    }


}
