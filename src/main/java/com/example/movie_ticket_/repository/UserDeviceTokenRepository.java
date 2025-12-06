package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.User;
import com.example.movie_ticket_.entity.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {


    @Transactional
    @Modifying
    void deleteByDeviceId(String deviceId);

    List<UserDeviceToken> findByUser(User user);
}
