package com.example.movie_ticket_.service;

import com.example.movie_ticket_.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(User user);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    User getUserById(Long id);
    List<User> getAllUsers();
    User changeUserStatus(Long id, int status);
    String encodePassword(String rawPassword);
    String generateRandomPassword(int length);
    boolean matchesPassword(String rawPassword, String encodedPassword);
     User findByEmail(String email);
}
