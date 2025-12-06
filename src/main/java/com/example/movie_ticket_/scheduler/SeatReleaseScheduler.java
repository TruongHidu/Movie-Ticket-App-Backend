package com.example.movie_ticket_.scheduler;

import com.example.movie_ticket_.service.SeatStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatReleaseScheduler {

    private final SeatStatusService seatStatusService;

    // Chạy mỗi phút
    @Scheduled(fixedRate = 60000)
    public void releaseSeats() {
        seatStatusService.releaseExpiredSeats();
    }
}
