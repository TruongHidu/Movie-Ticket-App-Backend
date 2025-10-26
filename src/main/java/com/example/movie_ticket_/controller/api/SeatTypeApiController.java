package com.example.movie_ticket_.controller.api;

import com.example.movie_ticket_.entity.SeatType;
import com.example.movie_ticket_.service.SeatTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seat-types")
@RequiredArgsConstructor
public class SeatTypeApiController {
    private  final SeatTypeService seatTypeService;

    @GetMapping()
    public List<SeatType> getActiveSeatTypes(){
        return seatTypeService.getAll();
    }
}
