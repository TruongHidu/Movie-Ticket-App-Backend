package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.FoodAndDrink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodAndDrinkRepository extends JpaRepository<FoodAndDrink, Long> {
    List<FoodAndDrink> findByStatus(int status);

    List<FoodAndDrink> findByStatusAndQuantityGreaterThan(int status, int quantity);
}
