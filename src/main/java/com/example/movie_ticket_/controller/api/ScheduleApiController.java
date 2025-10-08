package com.example.movie_ticket_.controller.api;

import com.example.movie_ticket_.dto.MovieScheduleDTO;
import com.example.movie_ticket_.dto.ScheduleDTO;
import com.example.movie_ticket_.dto.ShowTimeScheduleDTO;
import com.example.movie_ticket_.entity.Schedule;
import com.example.movie_ticket_.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleApiController {

    private final ScheduleService scheduleService;

    /**
     * Lấy danh sách lịch chiếu theo id phim
     * - Ngày chiếu >= ngày hiện tại
     * - Status lịch chiếu = 1 (active)
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByMovie(
            @PathVariable int movieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate) {

        // Nếu không có fromDate, mặc định là ngày hiện tại
        if (fromDate == null) {
            fromDate = LocalDate.now();
        }

        List<Schedule> schedules = scheduleService.getActiveSchedulesByMovie((long) movieId, fromDate);

        // Convert to DTO
        List<ScheduleDTO> scheduleDTOs = schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(scheduleDTOs);
    }

    /**
     * Lấy danh sách lịch chiếu theo id phim và ngày cụ thể
     */
//    @GetMapping("/movie/{movieId}/date/{date}")
//    public ResponseEntity<List<ScheduleDTO>> getSchedulesByMovieAndDate(
//            @PathVariable int movieId,
//            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//
//        List<Schedule> schedules = scheduleService.getActiveSchedulesByMovieAndDate((long) movieId, date);
//
//        // Convert to DTO
//        List<ScheduleDTO> scheduleDTOs = schedules.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(scheduleDTOs);
//    }


    @GetMapping("/movie/{movieId}/date/{date}")
    public ResponseEntity<MovieScheduleDTO> getSchedulesByMovieAndDate(
            @PathVariable int movieId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Lấy danh sách lịch chiếu active cho phim + ngày cụ thể
        List<Schedule> schedules = scheduleService.getActiveSchedulesByMovieAndDate((long) movieId, date);

        if (schedules.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Lấy thông tin phim từ lịch chiếu đầu tiên (vì tất cả schedule đều cùng 1 movie)
        var movie = schedules.get(0).getMovie();

        // Convert list schedules -> List<ShowTimeScheduleDTO>
        List<ShowTimeScheduleDTO> showTimeScheduleDTOs = schedules.stream()
                .map(s -> new ShowTimeScheduleDTO(
                        s.getIdLichChieu(),
                        s.getShowTime().getKhungGio(),  // LocalTime
                        s.getNgayChieu(),               // LocalDate
                        s.getRoom().getTenPhongChieu()  // Tên phòng chiếu
                ))
                .collect(Collectors.toList());

        // Build MovieScheduleDTO
        MovieScheduleDTO movieScheduleDTO = new MovieScheduleDTO(
                movie.getIdPhim(),
                movie.getTenPhim(),
                movie.getTheloai(),
                movie.getThoiLuong(),
                movie.getDaoDien(),
                movie.getNamPhatHanh(),
                movie.getPoster(),
                movie.getDescrip(),
                movie.getDienVien(),
                showTimeScheduleDTOs
        );

        return ResponseEntity.ok(movieScheduleDTO);
    }


    /**
     * Lấy danh sách ngày chiếu có lịch cho một phim
     */
    @GetMapping("/movie/{movieId}/dates")
    public ResponseEntity<List<LocalDate>> getAvailableDatesByMovie(
            @PathVariable int movieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate) {

        if (fromDate == null) {
            fromDate = LocalDate.now();
        }

        List<LocalDate> dates = scheduleService.getAvailableDatesByMovie((long) movieId, fromDate);
        return ResponseEntity.ok(dates);
    }

    /**
     * Lấy thông tin chi tiết một lịch chiếu
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Long id) {
        Schedule schedule = scheduleService.getById(id);
        if (schedule == null || schedule.getStatus() != 1) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToDTO(schedule));
    }

    /**
     * Convert Schedule entity to ScheduleDTO
     */
    private ScheduleDTO convertToDTO(Schedule schedule) {
        return new ScheduleDTO(
                schedule.getIdLichChieu(),
                schedule.getShowTime().getKhungGio(),
                schedule.getNgayChieu(),
                schedule.getMovie().getTenPhim(),
                schedule.getMovie().getPoster(),
                schedule.getMovie().getThoiLuong(),
                schedule.getRoom().getTenPhongChieu(),
                schedule.getMovie().getTheloai());
    }


    /**
     * Lấy tất cả các ngày có lịch chiếu (status = 1)
     */
    @GetMapping("/dates")
    public ResponseEntity<List<LocalDate>> getAllAvailableDates(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate) {

        if (fromDate == null) {
            fromDate = LocalDate.now();
        }

        List<LocalDate> dates = scheduleService.getAllAvailableDates(fromDate);
        return ResponseEntity.ok(dates);
    }


    /**
     * Lấy danh sách phim có lịch chiếu trong 1 ngày (kèm idLichChieu từng khung giờ)
     */
    @GetMapping("/date/{date}/movies")
    public ResponseEntity<List<MovieScheduleDTO>> getMoviesWithSchedulesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleService.getMoviesWithSchedulesByDate(date));
    }




}