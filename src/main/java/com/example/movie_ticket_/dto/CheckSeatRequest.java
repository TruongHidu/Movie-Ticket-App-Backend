package com.example.movie_ticket_.dto;

import lombok.Data;
import java.util.List;

@Data
public class CheckSeatRequest {
    private Long idLichChieu;
    private List<String> seatCodes;
}
