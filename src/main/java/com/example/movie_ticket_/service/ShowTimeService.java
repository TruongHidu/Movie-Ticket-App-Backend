package com.example.movie_ticket_.service;

import com.example.movie_ticket_.entity.Room;
import com.example.movie_ticket_.entity.ShowTime;
import com.example.movie_ticket_.repository.ShowTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowTimeService {
    private final ShowTimeRepository showTimeRepository;

    public List<ShowTime> getAll() {
        return showTimeRepository.findAll();
    }

    public ShowTime getById(Long id) {
        return showTimeRepository.findById(id).orElse(null);
    }

    public void save(ShowTime showTime) {
        showTimeRepository.save(showTime);
    }

    public void delete(Long id) {
        showTimeRepository.deleteById(id);
    }

    public void changeStatus(Long id, int newStatus) {
        ShowTime showTime = showTimeRepository.findById(id).orElse(null);
        if (showTime != null) {
            showTime.setStatus(newStatus);
            showTimeRepository.save(showTime);
        }
    }

    public List<ShowTime> getActiveShowTimes() {
        return showTimeRepository.findByStatus(1);
    }
}
