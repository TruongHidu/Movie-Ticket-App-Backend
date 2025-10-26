package com.example.movie_ticket_.service;

import com.example.movie_ticket_.entity.SeatType;
import com.example.movie_ticket_.repository.SeatTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatTypeService {

    private final SeatTypeRepository seatTypeRepository;

    public List<SeatType> getAll() {
        return seatTypeRepository.findAll();
    }



    public SeatType getById(String id) {
        return seatTypeRepository.findById(id).orElse(null);
    }

    public SeatType save(SeatType seatType) {
        if (seatType.getCreatedAt() == null) {
            seatType.setCreatedAt(LocalDateTime.now());
        }
        seatType.setUpdatedAt(LocalDateTime.now());
        return seatTypeRepository.save(seatType);
    }

    public void delete(String id) {
        seatTypeRepository.deleteById(id);
    }
}
