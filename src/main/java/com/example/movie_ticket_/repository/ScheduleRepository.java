package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {



    List<Schedule> findAllByOrderByNgayChieuDesc();
    List<Schedule> findByStatusOrderByNgayChieuDesc(int status);

    // Kiểm tra trùng lịch chiếu (cùng phòng, cùng xuất chiếu, cùng ngày)
    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE " +
            "s.room.idPhongChieu = :roomId AND " +
            "s.showTime.idXuatChieu = :showTimeId AND " +
            "s.ngayChieu = :ngayChieu AND " +
            "s.idLichChieu != :excludeId")
    boolean existsDuplicate(@Param("roomId") String roomId,
            @Param("showTimeId") Long showTimeId,
            @Param("ngayChieu") LocalDate ngayChieu,
            @Param("excludeId") Long excludeId);

    // Overload method cho trường hợp thêm mới (không có excludeId)
    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE " +
            "s.room.idPhongChieu = :roomId AND " +
            "s.showTime.idXuatChieu = :showTimeId AND " +
            "s.ngayChieu = :ngayChieu")
    boolean existsDuplicate(@Param("roomId") String roomId,
            @Param("showTimeId") Long showTimeId,
            @Param("ngayChieu") LocalDate ngayChieu);

    // Kiểm tra xem phòng có lịch chiếu trùng thời gian không
    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE " +
            "s.room.idPhongChieu = :roomId AND " +
            "s.ngayChieu = :ngayChieu AND " +
            "s.showTime.idXuatChieu = :showTimeId AND " +
            "s.status = 1 AND " +
            "(:excludeId IS NULL OR s.idLichChieu != :excludeId)")
    boolean isRoomOccupied(@Param("roomId") String roomId,
            @Param("showTimeId") Long showTimeId,
            @Param("ngayChieu") LocalDate ngayChieu,
            @Param("excludeId") Long excludeId);

    /**
     * Lấy lịch chiếu active theo movieId và từ ngày chỉ định
     */
    @Query("SELECT s FROM Schedule s WHERE " +
            "s.movie.idPhim = :movieId AND " +
            "((s.ngayChieu > :fromDate) OR (s.ngayChieu = :fromDate AND s.showTime.khungGio > :currentTime)) AND " +
            "s.status = :status " +
            "ORDER BY s.ngayChieu, s.showTime.khungGio")
    List<Schedule> findByMovieIdAndNgayChieuGreaterThanEqualAndStatus(
            @Param("movieId") Long movieId,
            @Param("fromDate") LocalDate fromDate,
            @Param("currentTime") LocalTime currentTime,
            @Param("status") int status);

    /**
     * Lấy lịch chiếu active theo movieId và ngày cụ thể
     * - Nếu ngày = hôm nay: chỉ lấy suất chiếu sau giờ hiện tại
     * - Nếu ngày > hôm nay: lấy TẤT CẢ suất chiếu
     */
    @Query("SELECT s FROM Schedule s WHERE " +
            "s.movie.idPhim = :movieId AND " +
            "s.ngayChieu = :date AND " +
            "(:date > :today OR (:date = :today AND s.showTime.khungGio > :currentTime)) AND " +
            "s.status = :status " +
            "ORDER BY s.showTime.khungGio")
    List<Schedule> findByMovieIdAndNgayChieuAndStatus(
            @Param("movieId") Long movieId,
            @Param("date") LocalDate date,
            @Param("today") LocalDate today,
            @Param("currentTime") LocalTime currentTime,
            @Param("status") int status);

    /**
     * Lấy tất cả lịch chiếu active theo ngày chỉ định
     */
    @Query("SELECT s FROM Schedule s " +
            "WHERE s.ngayChieu = :date AND " +
            "(:date > :today OR (:date = :today AND s.showTime.khungGio > :currentTime)) AND " +
            "s.status = :status " +
            "ORDER BY s.movie.idPhim, s.showTime.khungGio")
    List<Schedule> findActiveSchedulesByDate(
            @Param("date") LocalDate date,
            @Param("today") LocalDate today,
            @Param("currentTime") LocalTime currentTime,
            @Param("status") int status);
    /**
     * Lấy danh sách ngày chiếu có lịch cho một phim
     */
    @Query("SELECT DISTINCT s.ngayChieu FROM Schedule s WHERE " +
            "s.movie.idPhim = :movieId AND " +
            "((s.ngayChieu > :fromDate) OR (s.ngayChieu = :fromDate AND s.showTime.khungGio > :currentTime)) AND " +
            "s.status = :status " +
            "ORDER BY s.ngayChieu")
    List<LocalDate> findDistinctNgayChieuByMovieIdAndNgayChieuGreaterThanEqualAndStatus(
            @Param("movieId") Long movieId,
            @Param("fromDate") LocalDate fromDate,
            @Param("currentTime") LocalTime currentTime,
            @Param("status") int status);

    /**
     * Lấy tất cả ngày có lịch chiếu (status = 1)
     */
    @Query("SELECT DISTINCT s.ngayChieu FROM Schedule s " +
            "WHERE ((s.ngayChieu > :fromDate) OR (s.ngayChieu = :fromDate AND s.showTime.khungGio > :currentTime)) " +
            "AND s.status = :status " +
            "ORDER BY s.ngayChieu")
    List<LocalDate> findDistinctNgayChieuByNgayChieuGreaterThanEqualAndStatus(
            @Param("fromDate") LocalDate fromDate,
            @Param("currentTime") LocalTime currentTime,
            @Param("status") int status);

    /**
     * Lấy tất cả lịch chiếu active theo ngày chỉ định
     */
    @Query("SELECT s FROM Schedule s " +
            "WHERE (s.ngayChieu > :date OR  ( s.ngayChieu = :date AND s.showTime.khungGio > :currentTime)) AND " +
            "s.status = :status " +
            "ORDER BY s.movie.idPhim, s.showTime.khungGio")
    List<Schedule> findActiveSchedulesByDate(
            @Param("date") LocalDate date,
            @Param("currentTime") LocalTime currentTime,
            @Param("status") int status);




    @Query("SELECT s FROM Schedule s " +
            "WHERE s.ngayChieu = :date AND s.showTime.khungGio > :currentTime " +
            "AND s.status = :status " +
            "ORDER BY s.movie.idPhim, s.showTime.khungGio")
    List<Schedule> findActiveSchedulesForToday(
            @Param("date") LocalDate date,
            @Param("currentTime") LocalTime currentTime,
            @Param("status") int status);

    @Query("SELECT s FROM Schedule s " +
            "WHERE s.ngayChieu = :date AND s.status = :status " +
            "ORDER BY s.movie.idPhim, s.showTime.khungGio")
    List<Schedule> findActiveSchedulesByDateNoTime(
            @Param("date") LocalDate date,
            @Param("status") int status);

}
