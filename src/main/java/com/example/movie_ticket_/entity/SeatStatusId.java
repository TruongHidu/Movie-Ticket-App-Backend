package com.example.movie_ticket_.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatStatusId implements Serializable {

    @Column(name = "IDLICHCHIEU", nullable = false)
    private Long idLichChieu;

    @Column(name = "IDGHE", length = 10, nullable = false)
    private String idGhe;

    @Column(name = "IDPHONGCHIEU", nullable = false)
    private String idPhongChieu;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SeatStatusId that))
            return false;
        return Objects.equals(idLichChieu, that.idLichChieu)
                && Objects.equals(idGhe, that.idGhe)
                && Objects.equals(idPhongChieu, that.idPhongChieu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLichChieu, idGhe, idPhongChieu);
    }
}
