package com.example.movie_ticket_.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lichchieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDLICHCHIEU")
    private Long idLichChieu;

    @ManyToOne
    @JoinColumn(name = "IDPHIM", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "IDXUATCHIEU", nullable = false)
    private ShowTime showTime;

    @ManyToOne
    @JoinColumn(name = "IDPHONGCHIEU", nullable = false)
    private Room room;

    @Column(name = "NGAYCHIEU", nullable = false)
    private LocalDate ngayChieu;

    @Column(name = "status", nullable = false)
    private int status = 1;
}
