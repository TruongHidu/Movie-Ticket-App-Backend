package com.example.movie_ticket_.controller.api;

import com.example.movie_ticket_.dto.HoldSeatRequest;
import com.example.movie_ticket_.dto.SeatStatusDTO;
import com.example.movie_ticket_.entity.User;
import com.example.movie_ticket_.service.SeatStatusService;
import com.example.movie_ticket_.service.UserService; // Import UserService
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Import
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatStatusApiController {

    private final SeatStatusService seatStatusService;
    private final UserService userService; // Inject UserService to find ID from email

    @GetMapping("/schedule/{idLichChieu}")
    public ResponseEntity<List<SeatStatusDTO>> getSeatsBySchedule(@PathVariable Long idLichChieu) {
        List<SeatStatusDTO> seats = seatStatusService.getSeatsBySchedule(idLichChieu);
        return ResponseEntity.ok(seats);
    }

    // Giữ ghế - CHỈNH SỬA ĐỂ SỬ DỤNG EMAIL TỪ TOKEN
    @PostMapping("/hold")
    public ResponseEntity<String> holdSeat(@RequestBody HoldSeatRequest request,
                                           @AuthenticationPrincipal String authenticatedEmail) {

        // 1. Lấy thông tin User từ email được xác thực trong JWT Principal
        if (authenticatedEmail == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }

        // Dùng UserService để tìm User entity (Giống logic trong UserApiController)
        User currentUser = userService.findByEmail(authenticatedEmail);

        if (currentUser == null) {
            // Lỗi nghiêm trọng: Token hợp lệ nhưng user không tồn tại trong DB
            return ResponseEntity.status(403).body("Authenticated user not found in database");
        }

        long authenticatedUserId = currentUser.getId(); // Lấy ID của người dùng đã xác thực

        // 2. Gọi Service với ID của người dùng đã được xác thực, KHÔNG PHẢI ID từ request body
        boolean success = seatStatusService.holdSeat(
                request.getIdLichChieu(),
                request.getIdGhe(),
                request.getIdPhongChieu(),
                authenticatedUserId
        );

        return success ? ResponseEntity.ok("Seat held successfully")
                : ResponseEntity.badRequest().body("Seat not available or already held by someone else");
    }


    // Bỏ giữ ghế - Áp dụng logic tương tự
    @PostMapping("/unhold")
    public ResponseEntity<String> unholdSeat(@RequestBody HoldSeatRequest request,
                                             @AuthenticationPrincipal String authenticatedEmail) {

        if (authenticatedEmail == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }

        User currentUser = userService.findByEmail(authenticatedEmail);
        if (currentUser == null) {
            return ResponseEntity.status(403).body("Authenticated user not found in database");
        }

        long authenticatedUserId = currentUser.getId();

        boolean success = seatStatusService.unholdSeat(
                request.getIdLichChieu(),
                request.getIdGhe(),
                request.getIdPhongChieu(),
                authenticatedUserId // Dùng ID được trích xuất từ token
        );
        return success ? ResponseEntity.ok("Seat unhold successfully")
                : ResponseEntity.badRequest().body("Seat is held by another user or is not currently held by you");
    }

    // Đặt ghế - Áp dụng logic tương t
    @PostMapping("/book")
    public ResponseEntity<String> bookSeat(@RequestBody HoldSeatRequest request,
                                           @AuthenticationPrincipal String authenticatedEmail) {

        if (authenticatedEmail == null) {
            return ResponseEntity.status(401).build();
        }

        User currentUser = userService.findByEmail(authenticatedEmail);
        if (currentUser == null) {
            return ResponseEntity.status(403).body("Authenticated user not found in database");
        }

        long authenticatedUserId = currentUser.getId();

        boolean success = seatStatusService.bookSeat(
                request.getIdLichChieu(),
                request.getIdGhe(),
                request.getIdPhongChieu(),
                authenticatedUserId // Dùng ID được trích xuất từ token
        );
        return success ? ResponseEntity.ok("Seat booked successfully")
                : ResponseEntity.badRequest().body("Seat not available");
    }
}
