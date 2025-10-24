package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.dto.BannerRequest;
import com.example.movie_ticket_.service.BannerService;
import com.example.movie_ticket_.service.MovieService;
import com.example.movie_ticket_.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
public class BannerAdminController {

    private final BannerService bannerService;
    private final MovieService movieService;
    private final PromotionService promotionService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("banners", bannerService.getAll());
        model.addAttribute("movies", movieService.getActiveMovies());
        model.addAttribute("promotions", promotionService.getActivePromotions());
        model.addAttribute("bannerRequest", new BannerRequest());
        return "banner/list";
    }

    @PostMapping("/add")
    public String addBanner(@ModelAttribute BannerRequest request,
                            @RequestParam("image") MultipartFile image) {
        request.setImage(image);
        bannerService.create(request);
        return "redirect:/admin/banners";
    }

    @PostMapping("/update/{id}")
    public String updateBanner(@PathVariable Long id,
                               @ModelAttribute BannerRequest request,
                               @RequestParam(value = "image", required = false) MultipartFile image) {
        request.setImage(image);
        bannerService.update(id, request);
        return "redirect:/admin/banners";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        bannerService.delete(id);
        return "redirect:/admin/banners";
    }
}
