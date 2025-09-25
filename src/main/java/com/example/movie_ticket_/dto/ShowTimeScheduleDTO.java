package com.example.movie_ticket_.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowTimeScheduleDTO {
    private Long idLichChieu;
    private LocalTime gioChieu;
    private LocalDate ngayChieu;
    private String tenPhongChieu; // tiện nếu muốn hiển thị phòng
}
