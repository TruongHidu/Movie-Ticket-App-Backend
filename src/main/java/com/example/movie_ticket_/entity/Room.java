// PhongChieu.java
package com.example.movie_ticket_.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phongchieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @Column(name = "IDPHONGCHIEU", length = 10)
    private String idPhongChieu;

    @Column(name = "TENPHONGCHIEU", nullable = false, length = 100)
    private String tenPhongChieu;

    @Column(name = "status", nullable = false)
    private Integer status = 1;
}
