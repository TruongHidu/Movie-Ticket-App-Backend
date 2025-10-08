package com.example.movie_ticket_.dto;

import lombok.Data;

@Data
public class HoldSeatRequest {
    private Long idLichChieu;
    private String idGhe;
    private String idPhongChieu;
    private int userId;
}
