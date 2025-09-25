package com.example.movie_ticket_.service;

import com.example.movie_ticket_.dto.SeatStatusDTO;
import com.example.movie_ticket_.entity.SeatStatus;
import com.example.movie_ticket_.repository.SeatStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatStatusService {

    private final SeatStatusRepository seatStatusRepository;

    public List<SeatStatusDTO> getSeatsBySchedule(Long idLichChieu) {
        List<SeatStatus> seats = seatStatusRepository.findBySchedule_IdLichChieu(idLichChieu);

        return seats.stream().map(seat -> SeatStatusDTO.builder()
                .idGhe(seat.getSeatNumber().getId().getIdGhe())
                .idPhongChieu(seat.getSeatNumber().getId().getIdPhongChieu())
                .loaiGhe(seat.getSeatNumber().getLoaiGhe().getTenLoaiGhe())
                .status(seat.getStatus())
                .build()).collect(Collectors.toList());
    }
}
