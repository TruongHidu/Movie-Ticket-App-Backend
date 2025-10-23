package com.example.movie_ticket_.controller.api;

import com.example.movie_ticket_.entity.FoodAndDrink;
import com.example.movie_ticket_.service.FoodAndDrinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/food-drink")
@RequiredArgsConstructor
public class FoodAndDrinkApiController {

    private final FoodAndDrinkService foodAndDrinkService;


    @GetMapping("/available")
    public List<FoodAndDrink> getAvailableCombos() {
        return foodAndDrinkService.getActiveCombos();
    }
}
