package com.example.movie_ticket_.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.movie_ticket_.entity.ShowTime;

public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {

    List<ShowTime> findByStatus(Integer status);

}
