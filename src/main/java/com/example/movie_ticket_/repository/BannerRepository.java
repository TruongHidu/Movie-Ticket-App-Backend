package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByStatus(int status);
}
