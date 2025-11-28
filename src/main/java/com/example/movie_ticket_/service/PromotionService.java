package com.example.movie_ticket_.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


import com.example.movie_ticket_.entity.Promotion;
import com.example.movie_ticket_.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PromotionService {

    @Autowired
    private final  PromotionRepository promotionRepository;
    private final   NotificationService notificationService ;
    private final CloudinaryService cloudinaryService;

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public List<Promotion> getActivePromotions(){
        return  promotionRepository.findByStatusOrderByCreatedAtDesc(1);
    }

    public Optional<Promotion> getPromotionById(Long id) {
        return promotionRepository.findById(id);
    }

    public Promotion createPromotion(Promotion promotion,  MultipartFile image) {
        promotion.setCreatedAt(LocalDateTime.now());

        if (!image.isEmpty()) {
            Map uploadResult = cloudinaryService.uploadToFolder(image, "promotions");
            promotion.setImageUrl((String) uploadResult.get("secure_url"));
        }
        Promotion saved = promotionRepository.save(promotion);

        try {
            notificationService.sendPromotionNotification(
                    "Khuyến mãi mới!",
                    promotion.getTitle(),
                    promotion.getImageUrl()
            );
        } catch (Exception e) {
            System.out.println("Lỗi gửi thông báo: " + e.getMessage());
        }

        return saved;
    }

    public Promotion updatePromotion(Long id, Promotion updatedPromotion) {
        return promotionRepository.findById(id).map(promotion -> {
            promotion.setTitle(updatedPromotion.getTitle());
            promotion.setDescription(updatedPromotion.getDescription());
            promotion.setStatus(updatedPromotion.getStatus());

            // ⚠️ chỉ cập nhật ảnh nếu có ảnh mới
            if (updatedPromotion.getImageUrl() != null && !updatedPromotion.getImageUrl().isEmpty()) {
                promotion.setImageUrl(updatedPromotion.getImageUrl());
            }

            return promotionRepository.save(promotion);
        }).orElseThrow(() -> new RuntimeException("Promotion not found"));
    }

    public Promotion getPromotionById(long id){
        return promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie with id " + id + " not found"));
    }

    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }
}

