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
        LocalDateTime threshold = now.plusMinutes(15);

        List<Invoice> invoices = invoiceRepository.findInvoicesForNotify(now, threshold);

        for (Invoice invoice : invoices) {

            List<UserDeviceToken> devices =
                    deviceTokenRepo.findByUser(invoice.getUser());

            if (devices.isEmpty()) continue;

            for (UserDeviceToken device : devices) {
                String fcmToken = device.getFcmToken();

                if (fcmToken == null || fcmToken.isBlank()) continue;

                String title = "Sắp đến giờ xem phim!";
                String body = "Phim " + invoice.getMovieName() +
                        " chiếu lúc " + invoice.getShowTime() +
                        ". Ghế: " + invoice.getSeatCodes();

                try {
                    fcmService.sendNotification(fcmToken, title, body);
                } catch (FirebaseMessagingException e) {

                    if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED ||
                            e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {

                        deviceTokenRepo.delete(device);
                    }
                }

            }

            // cập nhật trạng thái đã gửi
            invoice.setStatus(2);
            invoiceRepository.save(invoice);
        }
    }

}
