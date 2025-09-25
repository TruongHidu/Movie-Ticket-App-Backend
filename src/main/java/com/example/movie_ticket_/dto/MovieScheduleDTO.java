package com.example.movie_ticket_.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieScheduleDTO {
    private int idPhim;
    private String tenPhim;
    private String theloai;
    private int thoiLuong;
    private String daoDien;
    private LocalDate namPhatHanh;
    private String poster;
    private String descrip;
    private String dienVien;
    private List<ShowTimeScheduleDTO> schedules;
}
