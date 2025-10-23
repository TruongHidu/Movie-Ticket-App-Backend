package com.example.movie_ticket_.service;

import com.example.movie_ticket_.entity.FoodAndDrink;
import com.example.movie_ticket_.repository.FoodAndDrinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodAndDrinkService {
    private final FoodAndDrinkRepository foodAndDrinkRepository;

    public List<FoodAndDrink> getAll(){
        return foodAndDrinkRepository.findAll();
    }

    public List<FoodAndDrink> getActiveCombos(){
        return foodAndDrinkRepository.findByStatus(1);
    }

    public List<FoodAndDrink> getInactiveCombos(){
        return  foodAndDrinkRepository.findByStatus(0);
    }
    // ✅ Mới: chỉ lấy combo đang hoạt động và còn hàng
//    public List<FoodAndDrink> getAvailableCombos() {
//        return foodAndDrinkRepository.findByStatusAndQuantityGreaterThan(1, 0);
//    }

    public FoodAndDrink getById(Long id){
        return  foodAndDrinkRepository.findById(id).orElse(null);
    }

    public  FoodAndDrink save(FoodAndDrink combo){
        return foodAndDrinkRepository.save(combo);
    }

    public FoodAndDrink update(Long id, FoodAndDrink combo){
        FoodAndDrink existing = getById(id);
        if (existing==null) return null;

        existing.setNameCombo(combo.getNameCombo());
        existing.setImageUrl(combo.getImageUrl());
        existing.setDescript(combo.getDescript());
        existing.setPrice(combo.getPrice());
        existing.setStatus(combo.getStatus());

        return foodAndDrinkRepository.save(existing);
    }

    public void changeStatus (Long id, int newStatus){
        FoodAndDrink combo = getById(id);
        if(combo!=null){
            combo.setStatus(newStatus);
            foodAndDrinkRepository.save(combo);
        }
    }

}
