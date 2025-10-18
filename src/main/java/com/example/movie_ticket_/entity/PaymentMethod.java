package com.example.movie_ticket_.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PAYMENT_METHOD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nameMethod;

    @Column(name= "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private int status = 1; // default active




}
