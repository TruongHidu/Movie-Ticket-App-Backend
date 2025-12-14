package com.example.movie_ticket_.controller.api;


import com.example.movie_ticket_.dto.CheckSeatRequest;
import com.example.movie_ticket_.dto.InvoiceDTO;
import com.example.movie_ticket_.dto.InvoiceRequest;
import com.example.movie_ticket_.entity.Invoice;
import com.example.movie_ticket_.entity.User;
import com.example.movie_ticket_.service.InvoiceService;
import com.example.movie_ticket_.service.UserService;
import com.google.api.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceApiController {

    private final InvoiceService invoiceService;
    private final UserService userService;

    @PostMapping("/check-seats")
    public ResponseEntity<?> checkSeats(@RequestBody CheckSeatRequest request) {
        List<String> conflictedSeats = invoiceService.checkSeatsAvailability(
                request.getIdLichChieu(),
                request.getSeatCodes()
        );

        if (conflictedSeats.isEmpty()) {
            return ResponseEntity.ok(Map.of("status", "OK", "message", "Tất cả ghế đều khả dụng"));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "Một số ghế đã có người giữ hoặc đặt",
                    "conflictedSeats", conflictedSeats
            ));
        }
    }


    @PostMapping("/create-payment-vnpay")
    public ResponseEntity<?> createInvoiceAndPayment(@RequestBody InvoiceRequest request) {
        try {
            return ResponseEntity.ok(invoiceService.createInvoiceAndPaymentWithVNPay(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/retry-payment/{invoiceId}")
    public ResponseEntity<?> retryPayment(
            @PathVariable Long invoiceId,
            @AuthenticationPrincipal String authenticatedEmail) {

        if (authenticatedEmail == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        try {
            Map<String, String> data = invoiceService.retryPayment(invoiceId, authenticatedEmail);

            return ResponseEntity.ok(data);
            // Luôn trả JSON: { "paymentUrl": "..." }

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal server error"
            ));
        }
    }



    @GetMapping("/my-invoices/count")
    public ResponseEntity<?> getTotalPendingPayment(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        String email = authentication.getName();
        return ResponseEntity.ok(invoiceService.getTotalPendingPayment(email));
    }


    @GetMapping("/my-invoices")
    public ResponseEntity<?> getMyInvoices(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        String email = authentication.getName();
        return ResponseEntity.ok(invoiceService.getInvoicesByEmail(email));
    }

    @GetMapping("/my-invoices/{id}")
    public ResponseEntity<?> getMyInvoiceById(
            @PathVariable Long id,
            org.springframework.security.core.Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Unauthorized"));
        }

        String email = authentication.getName();

        try {
            InvoiceDTO invoice = invoiceService.getInvoiceByIdAndEmail(id, email);
            return ResponseEntity.ok(invoice);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", e.getMessage()));
        }
    }


    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelInvoice(
            @PathVariable Long id,
            org.springframework.security.core.Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Unauthorized"));
        }

        String email = authentication.getName();

        try {
            InvoiceDTO canceledInvoice = invoiceService.cancelInvoice(id, email);
            return ResponseEntity.ok(Map.of(
                    "message", "Hủy hóa đơn thành công"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
