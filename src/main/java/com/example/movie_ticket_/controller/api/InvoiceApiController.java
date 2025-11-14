package com.example.movie_ticket_.controller.api;


import com.example.movie_ticket_.dto.CheckSeatRequest;
import com.example.movie_ticket_.dto.InvoiceDTO;
import com.example.movie_ticket_.dto.InvoiceRequest;
import com.example.movie_ticket_.entity.Invoice;
import com.example.movie_ticket_.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceApiController {

    private final InvoiceService invoiceService;

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


    @GetMapping("/my-invoices")
    public ResponseEntity<?> getMyInvoices(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        String email = authentication.getName(); // ✅ Lấy từ JWT subject
        return ResponseEntity.ok(invoiceService.getInvoicesByEmail(email));
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
