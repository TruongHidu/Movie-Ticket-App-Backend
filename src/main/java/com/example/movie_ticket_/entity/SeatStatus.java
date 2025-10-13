package com.example.movie_ticket_.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trangthaighe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatStatus {

    @EmbeddedId
    private SeatStatusId id; // gồm IDLICHCHIEU + IDGHE + IDPHONGCHIEU

    @ManyToOne
    @MapsId("idLichChieu")
    @JoinColumn(name = "IDLICHCHIEU", nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "IDGHE", referencedColumnName = "IDGHE", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "IDPHONGCHIEU", referencedColumnName = "IDPHONGCHIEU", nullable = false, insertable = false, updatable = false)
    })
    private SeatNumber seatNumber;

    @Column(name = "STATUS", nullable = false)
    private Integer status = 0;
    // 0 = trống, 1 = đã đặt, 2 = đang giữ, 3 = hỏng...
    @Column(name = "HOLD_UNTIL")
    private LocalDateTime holdUntil;

    @Column(name = "HOLD_BY")
    private Long holdBy; // user đang giữ ghế

}
