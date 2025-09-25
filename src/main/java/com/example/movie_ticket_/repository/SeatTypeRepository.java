package com.example.movie_ticket_.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.movie_ticket_.entity.SeatType;

public interface SeatTypeRepository extends JpaRepository<SeatType, String> {
}
