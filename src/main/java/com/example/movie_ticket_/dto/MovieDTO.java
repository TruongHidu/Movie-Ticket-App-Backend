
package com.example.movie_ticket_.dto;
import com.example.movie_ticket_.dto.ScheduleDTO;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class MovieDTO {
    private int idPhim;
    private String tenPhim;
    private String theloai;
    private int thoiLuong;
    private String daoDien;
    private LocalDate namPhatHanh;
    private String poster;
    private String descrip;
    private String dienVien;
    private List<ScheduleDTO> schedules;
}
