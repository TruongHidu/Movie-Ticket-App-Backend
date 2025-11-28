package com.example.movie_ticket_.service;

import com.example.movie_ticket_.dto.MovieScheduleDTO;
import com.example.movie_ticket_.dto.ShowTimeScheduleDTO;
import com.example.movie_ticket_.entity.Schedule;
import com.example.movie_ticket_.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<Schedule> getAll() {
        return scheduleRepository.findAllByOrderByNgayChieuDesc();
    }


    public List<Schedule> getByStatus(int status) {
        return scheduleRepository.findByStatusOrderByNgayChieuDesc(status);
    }



    public Schedule getById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    public boolean save(Schedule schedule) {
        // Kiểm tra trùng lịch
        boolean isDuplicate;
        if (schedule.getIdLichChieu() != null) {
            // Edit case - exclude current schedule
            isDuplicate = scheduleRepository.existsDuplicate(
                    schedule.getRoom().getIdPhongChieu(),
                    schedule.getShowTime().getIdXuatChieu(),
                    schedule.getNgayChieu(),
                    schedule.getIdLichChieu());
        } else {
            // Add new case
            isDuplicate = scheduleRepository.existsDuplicate(
                    schedule.getRoom().getIdPhongChieu(),
                    schedule.getShowTime().getIdXuatChieu(),
                    schedule.getNgayChieu());
        }

        if (isDuplicate) {
            return false; // Trùng lịch
        }

        scheduleRepository.save(schedule);
        return true; // Lưu thành công
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }

    public void changeStatus(Long id, int newStatus) {
        Schedule s = getById(id);
        if (s != null) {
            s.setStatus(newStatus);
            scheduleRepository.save(s);
        }
    }

    // Phương thức kiểm tra trùng lịch
    public boolean isScheduleConflict(String roomId, Long showTimeId, LocalDate ngayChieu, Long excludeId) {
        if (excludeId != null) {
            return scheduleRepository.existsDuplicate(roomId, showTimeId, ngayChieu, excludeId);
        } else {
            return scheduleRepository.existsDuplicate(roomId, showTimeId, ngayChieu);
        }
    }

    // Kiểm tra phòng có bận không
    public boolean isRoomOccupied(String roomId, Long showTimeId, LocalDate ngayChieu, Long excludeId) {
        return scheduleRepository.isRoomOccupied(roomId, showTimeId, ngayChieu, excludeId);
    }

    /**
     * Lấy danh sách lịch chiếu active theo movieId và từ ngày chỉ định
     */
    public List<Schedule> getActiveSchedulesByMovie(Long movieId, LocalDate fromDate, LocalTime currentTime) {
        return scheduleRepository.findByMovieIdAndNgayChieuGreaterThanEqualAndStatus(
                movieId, fromDate, currentTime, 1);
    }

    /**
     * Lấy danh sách lịch chiếu active theo movieId và ngày cụ thể
     */
    public List<Schedule> getActiveSchedulesByMovieAndDate(Long movieId, LocalDate date, LocalTime currentTime) {
        LocalDate today = LocalDate.now();
        return scheduleRepository.findByMovieIdAndNgayChieuAndStatus(
                movieId, date, today, currentTime, 1);
    }

    /**
     * Lấy danh sách ngày chiếu có lịch cho một phim
     */
    public List<LocalDate> getAvailableDatesByMovie(Long movieId, LocalDate fromDate, LocalTime currentTime) {
        return scheduleRepository.findDistinctNgayChieuByMovieIdAndNgayChieuGreaterThanEqualAndStatus(
                movieId, fromDate, currentTime, 1);
    }


    /**
     * Lấy tất cả ngày chiếu có lịch (status = 1)
     */
    public List<LocalDate> getAllAvailableDates(LocalDate fromDate, LocalTime currentTime) {
        return scheduleRepository.findDistinctNgayChieuByNgayChieuGreaterThanEqualAndStatus(fromDate,currentTime, 1);
    }


    public List<MovieScheduleDTO> getMoviesWithSchedulesByDate(LocalDate date, LocalTime currentTime) {


        int status = 1;
        List<Schedule> schedules;


        if (date.isEqual(LocalDate.now())) {
            // hôm nay → xét thời gian
            schedules = scheduleRepository.findActiveSchedulesForToday(date, currentTime, status);
        } else {
            // ngày khác → không xét thời gian
            schedules = scheduleRepository.findActiveSchedulesByDateNoTime(date, status);
        }

        Map<Integer, List<Schedule>> grouped = schedules.stream()
                .collect(Collectors.groupingBy(s -> s.getMovie().getIdPhim()));

        List<MovieScheduleDTO> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Schedule>> entry : grouped.entrySet()) {
            Schedule first = entry.getValue().get(0);

            List<ShowTimeScheduleDTO> showTimes = entry.getValue().stream()
                    .map(s -> new ShowTimeScheduleDTO(
                            s.getIdLichChieu(),
                            s.getShowTime().getKhungGio(),
                            s.getNgayChieu(),
                            s.getRoom().getTenPhongChieu()
                    ))
                    .sorted(Comparator.comparing(ShowTimeScheduleDTO::getGioChieu))
                    .toList();

            result.add(new MovieScheduleDTO(
                    first.getMovie().getIdPhim(),
                    first.getMovie().getTenPhim(),
                    first.getMovie().getTheloai(),
                    first.getMovie().getThoiLuong(),
                    first.getMovie().getDaoDien(),
                    first.getMovie().getNamPhatHanh(),
                    first.getMovie().getPoster(),
                    first.getMovie().getDescrip(),
                    first.getMovie().getDienVien(),
                    showTimes
            ));
        }
        return result;
    }


}