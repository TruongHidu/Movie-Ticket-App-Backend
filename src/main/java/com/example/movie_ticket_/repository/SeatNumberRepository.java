
package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.SeatNumber;
import com.example.movie_ticket_.entity.SeatNumberId;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// SeatNumberRepository.java - Fixed
public interface SeatNumberRepository extends JpaRepository<SeatNumber, SeatNumberId> {
    List<SeatNumber> findByPhongChieu_IdPhongChieu(String roomId);

    List<SeatNumber> findById_IdPhongChieu(String roomId); // Alternative query
}