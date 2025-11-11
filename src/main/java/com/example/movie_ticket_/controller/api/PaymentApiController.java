package com.example.movie_ticket_.controller.api;

import com.example.movie_ticket_.entity.Invoice;
import com.example.movie_ticket_.entity.SeatStatus;
import com.example.movie_ticket_.entity.User;
import com.example.movie_ticket_.repository.InvoiceRepository;
import com.example.movie_ticket_.repository.SeatStatusRepository;
import com.example.movie_ticket_.repository.UserRepository;
import com.example.movie_ticket_.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentApiController {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final SeatStatusRepository seatStatusRepository;
    private final EmailService emailService;

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> handleVNPayReturn(
            @RequestParam Map<String, String> params) {

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        String transactionNo = params.get("vnp_TransactionNo");

        Invoice invoice = invoiceRepository.findById(Long.parseLong(txnRef))
                .orElse(null);
        if (invoice == null) return ResponseEntity.badRequest().body("Invoice not found");

        if ("00".equals(responseCode)) {
            User user = invoice.getUser();
            System.out.println("Sending email to: " + user.getEmail());


            int usedPoint = invoice.getUsedPoint();
            if (usedPoint > 0) {
                if (user.getPoint() < usedPoint) {
                    throw new RuntimeException("Không đủ điểm để sử dụng");
                }
                user.setPoint(user.getPoint() - usedPoint);
            }
            int rewardPoints = invoice.getTotalPrice().intValue() / 100;
            user.setPoint(user.getPoint() + rewardPoints);
            user.setTotalSpent(user.getTotalSpent() + invoice.getTotalPrice().intValue());
            userRepository.save(user);

            List<String> seatCodes = List.of(invoice.getSeatCodes().split(","));
            List<SeatStatus> seatStatuses = seatStatusRepository.findByScheduleAndSeatCodes(invoice.getSchedule().getIdLichChieu(), seatCodes);
            for (SeatStatus s : seatStatuses) {
                s.setStatus(1); // 1 = đã đặt
                s.setHoldBy(user.getId());
                s.setHoldUntil(null);
            }

            invoice.setStatus(1); // PAID
            invoice.setTransactionNo(transactionNo);
            invoice.setPaymentTime(LocalDateTime.now());
            invoiceRepository.save(invoice);
            // Trong service xử lý thanh toán thành công
            emailService.sendSuccessPayment(
                    user.getEmail(),
                    user.getName(),
                    invoice
            );
            return ResponseEntity.ok("Payment success for invoice " + txnRef);
        } else {
            invoice.setStatus(2); // FAILED
            invoiceRepository.save(invoice);
            return ResponseEntity.badRequest().body("Payment failed");
        }
    }
}
