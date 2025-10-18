package com.example.movie_ticket_.controller.api;


import com.example.movie_ticket_.entity.PaymentMethod;
import com.example.movie_ticket_.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodApiController {

    private final PaymentMethodService paymentMethodService;

    /**
     * ✅ API: Lấy danh sách các phương thức thanh toán đang hoạt động (status = 1)
     * GET /api/payment-methods/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<PaymentMethod>> getActivePaymentMethods() {
        List<PaymentMethod> activeMethods = paymentMethodService.getAll()
                .stream()
                .filter(method -> method.getStatus() == 1)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activeMethods);
    }

    /**
     * ✅ API: Lấy chi tiết 1 phương thức thanh toán (nếu cần)
     * GET /api/payment-methods/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethod> getPaymentMethodById(@PathVariable Long id) {
        PaymentMethod method = paymentMethodService.getById(id);
        if (method == null || method.getStatus() != 1) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(method);
    }
}
