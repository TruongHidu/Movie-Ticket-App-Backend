package com.example.movie_ticket_.dto;

import com.example.movie_ticket_.entity.BannerType;
import lombok.Data;

@Data
public class BannerResponse {
    private Long id;
    private BannerType type;
    private Long movieId;
    private Long promotionId;
    private String imageUrl;
    private int status;
}
