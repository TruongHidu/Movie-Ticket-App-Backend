package com.example.movie_ticket_.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "phim")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPHIM")
    private int idPhim;

    @NotBlank
    @Size(max = 50)
    @Column(name = "TENPHIM")
    private String tenPhim;

    @NotBlank
    @Size(max = 100)
    @Column(name = "THELOAI")
    private String theloai;   // gộp thể loại vào luôn

    @Column(name = "THOILUONG")
    private int thoiLuong;    // phút

    @Size(max = 50)
    @Column(name = "DAODIEN")
    private String daoDien;

    // ✅ Lưu ngày/tháng/năm
    @JsonFormat(pattern = "dd/MM/yyyy") // hiển thị JSON dạng dd/MM/yyyy
    @Column(name = "NAMPH", columnDefinition = "DATE")
    private LocalDate namPhatHanh;

    @Size(max = 200)
    @Column(name = "POSTER")
    private String poster;

    @Lob
    @Column(name = "DESCRIP", columnDefinition = "TEXT")
    private String descrip;

    @Size(max = 500)
    @Column(name = "DIENVIEN")
    private String dienVien;

    @Size(max = 500)
    @Column(name = "TRAILER_URL", length = 500)
    private String trailerUrl;


    // 0 = khóa, 1 = đang hoạt động
    @Column(name = "STATUS")
    private int status = 0;


}
