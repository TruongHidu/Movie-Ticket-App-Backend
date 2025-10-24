package com.example.movie_ticket_.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="banners")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BannerType type;
    //nếu là movie
    private Long movieId;
    private Long promotionId;
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "STATUS", nullable = false)
    private int status = 1;

}
