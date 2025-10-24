package com.example.movie_ticket_.dto;

import com.example.movie_ticket_.entity.BannerType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BannerRequest {
    private BannerType type;
    private Long movieId;
    private Long promotionId;
    private MultipartFile image;
}
