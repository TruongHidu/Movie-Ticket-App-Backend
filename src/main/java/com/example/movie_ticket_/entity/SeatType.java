package com.example.movie_ticket_.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "loaighe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatType {

    @Id
    @Column(name = "IDLOAIGHE", length = 10, nullable = false)
    private String idLoaiGhe;

    @Column(name = "TENLOAIGHE", length = 100, nullable = false)
    private String tenLoaiGhe;

    @Column(name = "DONGIA", precision = 10, scale = 2, nullable = false)
    private BigDecimal donGia;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
