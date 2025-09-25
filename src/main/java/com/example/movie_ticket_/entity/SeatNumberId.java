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
public class SeatNumberId implements Serializable {

    @Column(name = "IDGHE", length = 10, nullable = false)
    private String idGhe; // A01, B02...

    @Column(name = "IDPHONGCHIEU", nullable = false)
    private String idPhongChieu;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SeatNumberId that))
            return false;
        return Objects.equals(idGhe, that.idGhe) &&
                Objects.equals(idPhongChieu, that.idPhongChieu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idGhe, idPhongChieu);
    }
}
