package com.example.movie_ticket_.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "masoghe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatNumber {

    @EmbeddedId
    private SeatNumberId id; // gồm IDGHE + IDPHONGCHIEU

    @ManyToOne
    @MapsId("idPhongChieu") // ánh xạ field IDPHONGCHIEU trong SeatNumberId
    @JoinColumn(name = "IDPHONGCHIEU", nullable = false)
    private Room phongChieu;

    @ManyToOne
    @JoinColumn(name = "IDLOAIGHE", nullable = false)
    private SeatType loaiGhe;
}
