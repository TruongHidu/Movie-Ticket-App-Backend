package com.example.movie_ticket_.controller.api;

import com.example.movie_ticket_.dto.HoldSeatRequest;
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

    // Giữ ghế
    @PostMapping("/hold")
    public ResponseEntity<String> holdSeat(@RequestBody HoldSeatRequest request) {
        boolean success = seatStatusService.holdSeat(
                request.getIdLichChieu(),
                request.getIdGhe(),
                request.getIdPhongChieu(),
                request.getUserId()

        );
        return success ? ResponseEntity.ok("Seat held successfully")
                : ResponseEntity.badRequest().body("Seat not available");
    }


    // Bỏ giữ ghế
    @PostMapping("/unhold")
    public ResponseEntity<String> unholdSeat(@RequestBody HoldSeatRequest request) {
        boolean success = seatStatusService.unholdSeat(
                request.getIdLichChieu(),
                request.getIdGhe(),
                request.getIdPhongChieu(),
                request.getUserId()
        );
        return success ? ResponseEntity.ok("Seat unhold successfully")
                : ResponseEntity.badRequest().body("Seat is held by another available");
    }

    // Đặt ghế
    @PostMapping("/book")
    public ResponseEntity<String> bookSeat(@RequestBody HoldSeatRequest request) {
        boolean success = seatStatusService.bookSeat(
                request.getIdLichChieu(),
                request.getIdGhe(),
                request.getIdPhongChieu(),
                request.getUserId()
        );
        return success ? ResponseEntity.ok("Seat booked successfully")
                : ResponseEntity.badRequest().body("Seat not available");
    }
}
