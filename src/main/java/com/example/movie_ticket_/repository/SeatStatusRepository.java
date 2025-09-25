package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.SeatStatus;
import com.example.movie_ticket_.entity.SeatStatusId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatStatusRepository extends JpaRepository<SeatStatus, SeatStatusId> {
    List<SeatStatus> findBySchedule_IdLichChieu(Long idLichChieu);
}
