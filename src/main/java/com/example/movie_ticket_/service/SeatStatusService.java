package com.example.movie_ticket_.service;

import com.example.movie_ticket_.dto.SeatStatusDTO;
import com.example.movie_ticket_.entity.SeatStatus;
import com.example.movie_ticket_.entity.SeatStatusId;
import com.example.movie_ticket_.repository.SeatStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatStatusService {

    private final SeatStatusRepository seatStatusRepository;

    // Lấy danh sách ghế
    public List<SeatStatusDTO> getSeatsBySchedule(Long idLichChieu) {
        List<SeatStatus> seats = seatStatusRepository.findBySchedule_IdLichChieu(idLichChieu);

        return seats.stream().map(seat -> {
            // Kiểm tra ghế hết hạn giữ
            if (seat.getStatus() == 2 && seat.getHoldUntil() != null
                    && seat.getHoldUntil().isBefore(LocalDateTime.now())) {
                seat.setStatus(0);
                seat.setHoldUntil(null);
                seat.setHoldBy(null);
                seatStatusRepository.save(seat);
            }

            return SeatStatusDTO.builder()
                    .idGhe(seat.getSeatNumber().getId().getIdGhe())
                    .idPhongChieu(seat.getSeatNumber().getId().getIdPhongChieu())
                    .loaiGhe(seat.getSeatNumber().getLoaiGhe().getTenLoaiGhe())
                    .status(seat.getStatus())
                    .donGia(seat.getSeatNumber().getLoaiGhe().getDonGia().toBigInteger())
                    .holdBy(seat.getHoldBy())
                    .build();
        }).collect(Collectors.toList());
    }

    // Giữ ghế
    public boolean holdSeat(Long idLichChieu, String idGhe, String idPhongChieu, long userId) {
        SeatStatusId seatId = new SeatStatusId(idLichChieu, idGhe, idPhongChieu);
        SeatStatus seat = seatStatusRepository.findById(seatId).orElse(null);

        if (seat != null && seat.getStatus() == 0) {
            seat.setStatus(2);
            seat.setHoldUntil(LocalDateTime.now().plusMinutes(10));
            seat.setHoldBy(userId);
            seatStatusRepository.save(seat);
            return true;
        }
        return false;
    }

    // Bỏ giữ
    public boolean unholdSeat(Long idLichChieu, String idGhe, String idPhongChieu, long userId) {
        SeatStatusId seatId = new SeatStatusId(idLichChieu, idGhe, idPhongChieu);
        SeatStatus seat = seatStatusRepository.findById(seatId).orElse(null);

        if (seat != null && seat.getStatus() == 2 && userId==seat.getHoldBy()) {
            seat.setStatus(0);
            seat.setHoldUntil(null);
            seat.setHoldBy(null);
            seatStatusRepository.save(seat);
            return true;
        }
        return false;
    }



    // Thanh toán thành công (đặt ghế)
    public boolean bookSeat(Long idLichChieu, String idGhe, String idPhongChieu, long userId) {
        SeatStatusId seatId = new SeatStatusId(idLichChieu, idGhe, idPhongChieu);
        SeatStatus seat = seatStatusRepository.findById(seatId).orElse(null);

        if (seat != null && (seat.getStatus() == 2 || seat.getStatus() == 0)) {
            seat.setStatus(1);
            seat.setHoldUntil(null);
            seatStatusRepository.save(seat);
            return true;
        }
        return false;
    }

    // Giải phóng ghế hết hạn
    public void releaseExpiredSeats() {
        List<SeatStatus> expiredSeats = seatStatusRepository
                .findByStatusAndHoldUntilBefore(2, LocalDateTime.now());

        expiredSeats.forEach(seat -> {
            seat.setStatus(0);
            seat.setHoldUntil(null);
            seat.setHoldBy(null);
        });

        if (!expiredSeats.isEmpty()) {
            seatStatusRepository.saveAll(expiredSeats);
        }
    }
}
