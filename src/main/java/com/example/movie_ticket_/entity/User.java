package com.example.movie_ticket_.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "point", nullable = false, columnDefinition = "INT DEFAULT 0")
    private long point = 0;

    @Column(name = "total_spent", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long totalSpent = 0L; // tổng chi tiêu của user

    @Column(nullable = true)
    private String password;



    private int status; // 0 = khóa, 1 = hoạt động

    private int role;


}
