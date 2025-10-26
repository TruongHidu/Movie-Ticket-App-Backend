package com.example.movie_ticket_.controller.api;

import com.example.movie_ticket_.entity.Promotion;
import com.example.movie_ticket_.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
@RequiredArgsConstructor
public class PromotionApiController {
    private final PromotionService promotionService;

    @GetMapping("/active")
    public List<Promotion> getActivePromotion(){
        return promotionService.getActivePromotions();
    }
}
