package com.example.movie_ticket_.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "phim")
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

    public int getIdPhim() {
        return idPhim;
    }

    public void setIdPhim(int idPhim) {
        this.idPhim = idPhim;
    }

    public String getTenPhim() {
        return tenPhim;
    }

    public void setTenPhim(String tenPhim) {
        this.tenPhim = tenPhim;
    }

    public String getTheloai() {
        return theloai;
    }

    public void setTheloai(String theloai) {
        this.theloai = theloai;
    }

    public int getThoiLuong() {
        return thoiLuong;
    }

    public void setThoiLuong(int thoiLuong) {
        this.thoiLuong = thoiLuong;
    }

    public String getDaoDien() {
        return daoDien;
    }

    public void setDaoDien(String daoDien) {
        this.daoDien = daoDien;
    }

    public LocalDate getNamPhatHanh() {
        return namPhatHanh;
    }

    public void setNamPhatHanh(LocalDate namPhatHanh) {
        this.namPhatHanh = namPhatHanh;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getDienVien() {
        return dienVien;
    }

    public void setDienVien(String dienVien) {
        this.dienVien = dienVien;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

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

    @Column(name = "DESCRIP", columnDefinition = "TEXT")
    private String descrip;

    @Size(max = 500)
    @Column(name = "DIENVIEN")
    private String dienVien;

    // 0 = khóa, 1 = đang hoạt động
    @Column(name = "STATUS")
    private int status = 0;
}
