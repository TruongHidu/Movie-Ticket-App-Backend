package com.example.movie_ticket_.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "promotion")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description; // sửa lại tên biến

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    // 1 = đang hoạt động, 0 = đã ẩn
    private int status = 1;
}
