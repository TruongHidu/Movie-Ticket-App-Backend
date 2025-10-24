package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.entity.Promotion;
import com.example.movie_ticket_.service.CloudinaryService;
import com.example.movie_ticket_.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/admin/promotions")
@RequiredArgsConstructor
public class PromotionAdminController {

    private final PromotionService promotionService;
    private final CloudinaryService cloudinaryService;

    // 🧾 Hiển thị danh sách khuyến mãi
    @GetMapping
    public String listPromotions(Model model) {
        model.addAttribute("promotions", promotionService.getAllPromotions());
        return "promotion/list";
    }

    // ➕ Form thêm khuyến mãi
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("promotion", new Promotion());
        return "promotion/add";
    }

    @PostMapping("/add")
    public String addPromotion(@ModelAttribute Promotion promotion,
                               @RequestParam("image") MultipartFile image) {
        if (!image.isEmpty()) {
            Map uploadResult = cloudinaryService.upload(image);
            promotion.setImageUrl((String) uploadResult.get("secure_url"));
        }
        promotionService.createPromotion(promotion);
        return "redirect:/admin/promotions";
    }

    // ✏️ Form sửa khuyến mãi
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Promotion promotion = promotionService.getPromotionById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi có id = " + id));
        model.addAttribute("promotion", promotion);
        return "promotion/edit";
    }

    // 💾 Cập nhật khuyến mãi
    @PostMapping("/edit/{id}")
    public String updatePromotion(@PathVariable Long id,
                                  @ModelAttribute Promotion promotion,
                                  @RequestParam("image") MultipartFile image) {
        if (!image.isEmpty()) {
            Map uploadResult = cloudinaryService.upload(image);
            promotion.setImageUrl((String) uploadResult.get("secure_url"));
        }
        promotionService.updatePromotion(id, promotion);
        return "redirect:/admin/promotions";
    }

    // 🗑️ Xóa khuyến mãi
    @GetMapping("/delete/{id}")
    public String deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return "redirect:/admin/promotions";
    }
}
