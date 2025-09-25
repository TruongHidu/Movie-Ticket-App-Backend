package com.example.movie_ticket_.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
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
    private int point = 0;

    @Column(name = "total_spent", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long totalSpent = 0L; // tổng chi tiêu của user

    @Column(nullable = true)
    private String password;



    private int status; // 0 = khóa, 1 = hoạt động

    private int role;

    // ====== Getter & Setter =======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPoint() { return point; }
    public void setPoint(int point) { this.point = point; }

    public long getTotalSpent() { return totalSpent; }
    public void setTotalSpent(long totalSpent) { this.totalSpent = totalSpent; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public int getRole() { return role; }
    public void setRole(int role) { this.role = role; }
}
