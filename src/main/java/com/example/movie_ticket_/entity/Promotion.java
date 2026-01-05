package com.example.movie_ticket_.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

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
    @Size(max = 500)
    @Column(name = "title", nullable = false, length=500)
    private String title;
    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description; // sửa lại tên biến

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 1 = đang hoạt động, 0 = đã ẩn
    private int status = 1;
}
