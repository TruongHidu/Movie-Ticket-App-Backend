package com.example.movie_ticket_.dto;

import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatStatusDTO {
    private String idGhe;
    private String idPhongChieu;
    private String loaiGhe; // tên loại ghế (VIP, Thường...)
    private Integer status; // 0 = trống, 1 = đã đặt, 2 = đang giữ, 3 = hỏng
    private BigInteger donGia;
    private Integer holdBy;
}
