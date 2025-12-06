package com.example.movie_ticket_.service;

import com.example.movie_ticket_.repository.UserDeviceTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserDeviceTokenRepository userDeviceTokenRepository;

    @Transactional
    public void deleteTokenByDeviceId(String deviceId) {
        userDeviceTokenRepository.deleteByDeviceId(deviceId);
    }
}
