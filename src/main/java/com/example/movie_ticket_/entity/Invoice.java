package com.example.movie_ticket_.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người đặt vé
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Lịch chiếu
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    // Snapshot thông tin
    @Column(name = "movie_name")
    private String movieName;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "show_date")
    private LocalDate showDate;

    @Column(name = "show_time")
    private LocalTime showTime;

    @Column(name = "seat_codes", columnDefinition = "TEXT")
    private String seatCodes;

    @Column(name = "food_drinks", columnDefinition = "TEXT")
    private String foodDrinks;

    @Column(name="payment_method")
    private long paymentMethodId;



    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    // ✅ Số điểm đã sử dụng để giảm giá
    @Column(name = "used_point")
    private int usedPoint;

    @Column(name="bonus_point")
    private int bonusPoint;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "status", nullable = false)
    private int status = 0;

    @Column(name = "transaction_no")
    private String transactionNo; // mã giao dịch do VNPay trả về

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

}
