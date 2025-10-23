package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.entity.FoodAndDrink;
import com.example.movie_ticket_.service.CloudinaryService;
import com.example.movie_ticket_.service.FoodAndDrinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/admin/food-drink")
@RequiredArgsConstructor
public class FoodAndDrinkAdminController {
    private final FoodAndDrinkService foodAndDrinkService;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public String list(Model model){
        model.addAttribute("combos", foodAndDrinkService.getAll());
        return "foodAndDrink/list";
    }

    @GetMapping("/add")
    public String addForm(Model model){
        model.addAttribute("combos", new FoodAndDrink());
        return "foodAndDrink/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute FoodAndDrink combo,
                      @RequestParam("imageFile")MultipartFile imageFile){
        try {
            if(imageFile != null && !imageFile.isEmpty()){
                Map uploadResult = cloudinaryService.upload(imageFile);
                String imageUrl = (String) uploadResult.get("secure_url");
                combo.setImageUrl(imageUrl);
            }
            foodAndDrinkService.save(combo);

        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/admin/food-drink";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model){
        FoodAndDrink combo = foodAndDrinkService.getById(id);
        if(combo==null){
            return "redirect:/admin/food-drink";
        }
        model.addAttribute("combo", combo);
        return "foodAndDrink/edit";
    }

    @PostMapping("/edit/{id}")
    public  String edit(@PathVariable Long id,
                        @RequestParam("nameCombo") String nameCombo,
                        @RequestParam("descript")String descript,
                        @RequestParam("price") BigDecimal price,
                        @RequestParam("status") int status,
                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile){
        try {
            FoodAndDrink combo = foodAndDrinkService.getById(id);
            if (combo != null) {
                combo.setNameCombo(nameCombo);
                combo.setDescript(descript);
                combo.setPrice(price);
                combo.setStatus(status);
                if (imageFile != null && !imageFile.isEmpty()) {
                    Map uploadResult = cloudinaryService.upload(imageFile);
                    combo.setImageUrl((String) uploadResult.get("secure_url"));
                }

                foodAndDrinkService.save(combo);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/food-drink";
    }

    @PostMapping("/status/{id}")
    public String changeStatus(@PathVariable Long id){
        FoodAndDrink combo = foodAndDrinkService.getById(id);
        if(combo!= null){
            int newStatus = combo.getStatus()==1 ? 0 : 1;
            foodAndDrinkService.changeStatus(id,newStatus);
        }
        return "redirect:/admin/food-drink";
    }
}
