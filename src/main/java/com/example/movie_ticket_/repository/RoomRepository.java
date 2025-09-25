
package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.Movie;
import com.example.movie_ticket_.entity.Room;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomRepository extends JpaRepository<Room, String> {

    @Query(value = "SELECT r.IDPHONGCHIEU FROM phongchieu r ORDER BY r.IDPHONGCHIEU DESC LIMIT 1", nativeQuery = true)
    String findLastId();

    List<Room> findByStatus(int status);

}
