package com.example.movie_ticket_.controller.api;

import com.example.movie_ticket_.dto.SeatStatusDTO;
import com.example.movie_ticket_.service.SeatStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatStatusApiController {

    private final SeatStatusService seatStatusService;

    @GetMapping("/schedule/{idLichChieu}")
    public ResponseEntity<List<SeatStatusDTO>> getSeatsBySchedule(@PathVariable Long idLichChieu) {
        List<SeatStatusDTO> seats = seatStatusService.getSeatsBySchedule(idLichChieu);
        return ResponseEntity.ok(seats);
    }
}
