package com.example.movie_ticket_.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "food_and_drink")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodAndDrink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_combo", nullable = false)
    private  String nameCombo;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "descript")
    private String descript;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "status")
    private Integer status;
}
