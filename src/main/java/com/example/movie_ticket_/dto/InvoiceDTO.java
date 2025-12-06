package com.example.movie_ticket_.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {
    private Long id;
    private String movieName;
    private String roomName;
    private LocalDate showDate;
    private LocalTime showTime;
    private String seatCodes;
    private String foodDrinks;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private int usedPoint;
    private int bonusPoint;
    private LocalDateTime paymentTime;
    private String barcodeUrl;
    private LocalDateTime createdAt;
    private int status;
}
