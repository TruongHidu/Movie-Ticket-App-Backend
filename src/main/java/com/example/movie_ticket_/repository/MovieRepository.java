package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByTenPhimContainingIgnoreCase(String tenPhim);

    List<Movie> findByTheloaiContainingIgnoreCase(String theloai);

    List<Movie> findByStatus(int status);

    // Tìm phim đang chiếu
    @Query("SELECT DISTINCT m FROM Movie m JOIN Schedule s ON m.idPhim = s.movie.idPhim " +
            "WHERE m.status = 1 AND s.status = 1 AND (s.ngayChieu > :date OR  ( s.ngayChieu = :date AND s.showTime.khungGio > :currentTime)) " +
            "AND m.namPhatHanh <= :date")
    List<Movie> findPlayingMovies( @Param("date") LocalDate date, @Param("currentTime") LocalTime currentTime);

    // Tìm phim sắp chiếu
    @Query("SELECT m FROM Movie m " +
            "WHERE m.status = 1 AND m.namPhatHanh > :currentDate")
    List<Movie> findUpcomingMovies(LocalDate currentDate);

}
