package com.example.movie_ticket_.controller.api;

import com.example.movie_ticket_.dto.BannerResponse;
import com.example.movie_ticket_.entity.Banner;
import com.example.movie_ticket_.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerApiController {

    private final BannerService bannerService;

    @GetMapping("/active")
    public List<Banner> getActiveBanners(){
        return bannerService.getActiveBanners();
    }
}
