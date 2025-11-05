package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.SeatStatus;
import com.example.movie_ticket_.entity.SeatStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatStatusRepository extends JpaRepository<SeatStatus, SeatStatusId> {
    List<SeatStatus> findBySchedule_IdLichChieu(Long idLichChieu);
    List<SeatStatus> findByStatusAndHoldUntilBefore(Integer status, LocalDateTime time);
    @Query("SELECT s FROM SeatStatus s WHERE s.schedule.idLichChieu = :scheduleId AND s.seatNumber.id.idGhe IN :seatCodes")
    List<SeatStatus> findByScheduleAndSeatCodes(
            @Param("scheduleId") Long scheduleId,
            @Param("seatCodes") List<String> seatCodes
    );

}
