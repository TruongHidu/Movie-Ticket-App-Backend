package com.example.movie_ticket_.service;

import com.example.movie_ticket_.dto.BannerRequest;
import com.example.movie_ticket_.dto.BannerResponse;
import com.example.movie_ticket_.entity.Banner;
import com.example.movie_ticket_.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final CloudinaryService cloudinaryService;

    public List<BannerResponse> getAll() {
        return bannerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BannerResponse create(BannerRequest request) {
        Banner banner = new Banner();
        banner.setType(request.getType());
        banner.setMovieId(request.getMovieId());
        banner.setPromotionId(request.getPromotionId());

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            Map uploadResult = cloudinaryService.uploadToFolder(request.getImage(), "banners");
            banner.setImageUrl((String) uploadResult.get("secure_url"));
        }

        bannerRepository.save(banner);
        return toResponse(banner);
    }

    @Transactional
    public BannerResponse update(Long id, BannerRequest request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));

        banner.setType(request.getType());
        banner.setMovieId(request.getMovieId());
        banner.setPromotionId(request.getPromotionId());

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            Map uploadResult = cloudinaryService.upload(request.getImage());
            banner.setImageUrl((String) uploadResult.get("secure_url"));
        }

        bannerRepository.save(banner);
        return toResponse(banner);
    }

    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }

    private BannerResponse toResponse(Banner banner) {
        BannerResponse res = new BannerResponse();
        res.setId(banner.getId());
        res.setType(banner.getType());
        res.setMovieId(banner.getMovieId());
        res.setPromotionId(banner.getPromotionId());
        res.setImageUrl(banner.getImageUrl());
        res.setStatus(banner.getStatus());
        return res;
    }
}
