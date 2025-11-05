package com.example.movie_ticket_.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceRequest {
    private Long userId;
    private Long scheduleId;

    private String seatCodes;
    private String foodDrinks;

    private long paymentMethodId;

    private BigDecimal totalPrice;
    private int usedPoint; // ✅ số điểm đã sử dụng
}
