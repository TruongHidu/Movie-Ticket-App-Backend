package com.example.movie_ticket_.controller.api;


import com.example.movie_ticket_.dto.CheckSeatRequest;
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


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Invoice>> getInvoicesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
