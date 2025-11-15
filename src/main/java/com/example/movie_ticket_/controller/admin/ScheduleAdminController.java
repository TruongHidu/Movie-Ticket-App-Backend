package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.entity.Movie;
import com.example.movie_ticket_.entity.Room;
import com.example.movie_ticket_.entity.Schedule;
import com.example.movie_ticket_.entity.ShowTime;
import com.example.movie_ticket_.service.MovieService;
import com.example.movie_ticket_.service.RoomService;
import com.example.movie_ticket_.service.ScheduleService;
import com.example.movie_ticket_.service.ShowTimeService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/schedules")
@RequiredArgsConstructor
public class ScheduleAdminController {

    private final ScheduleService scheduleService;
    private final MovieService movieService;
    private final RoomService roomService;
    private final ShowTimeService showTimeService;

    @GetMapping
    public String list(
            @RequestParam(value = "status", required = false) Integer status,
            Model model) {

        List<Schedule> schedules;

        if (status == null) {
            schedules = scheduleService.getAll(); // hoặc getActive
        } else {
            schedules = scheduleService.getByStatus(status);
        }

        model.addAttribute("currentStatus", status); // để hiển thị lại trên UI
        model.addAttribute("schedules", schedules);

        return "schedule/list";
    }


    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("movies", movieService.getActiveMovies());
        model.addAttribute("rooms", roomService.getActiveRooms());
        model.addAttribute("showTimes", showTimeService.getActiveShowTimes());
        return "schedule/add";
    }

    @PostMapping("/add")
    public String add(@RequestParam("movieId") int movieId,
            @RequestParam("roomId") String roomId,
            @RequestParam("showTimeId") Long showTimeId,
            @RequestParam("ngayChieu") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayChieu,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra trùng lịch
        boolean isConflict = scheduleService.isScheduleConflict(roomId, showTimeId, ngayChieu, null);

        if (isConflict) {
            redirectAttributes.addFlashAttribute("error",
                    "Lịch chiếu đã tồn tại cho phòng này, xuất chiếu và ngày đã chọn!");
            return "redirect:/admin/schedules/add";
        }

        // Tạo schedule mới
        Schedule schedule = new Schedule();
        schedule.setMovie(movieService.getMovieById(movieId));
        schedule.setRoom(roomService.getById(roomId));
        schedule.setShowTime(showTimeService.getById(showTimeId));
        schedule.setNgayChieu(ngayChieu);
        schedule.setStatus(1);

        boolean saved = scheduleService.save(schedule);
        if (saved) {
            redirectAttributes.addFlashAttribute("success", "Thêm lịch chiếu thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm lịch chiếu!");
        }

        return "redirect:/admin/schedules";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Schedule schedule = scheduleService.getById(id);
        if (schedule == null) {
            return "redirect:/admin/schedules";
        }

        model.addAttribute("schedule", schedule);
        model.addAttribute("movies", movieService.getActiveMovies());
        model.addAttribute("rooms", roomService.getActiveRooms());
        model.addAttribute("showTimes", showTimeService.getActiveShowTimes());

        return "schedule/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
            @RequestParam("movieId") int movieId,
            @RequestParam("roomId") String roomId,
            @RequestParam("showTimeId") Long showTimeId,
            @RequestParam("ngayChieu") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayChieu,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra trùng lịch (loại trừ bản ghi hiện tại)
        boolean isConflict = scheduleService.isScheduleConflict(roomId, showTimeId, ngayChieu, id);

        if (isConflict) {
            redirectAttributes.addFlashAttribute("error",
                    "Lịch chiếu đã tồn tại cho phòng này, xuất chiếu và ngày đã chọn!");
            return "redirect:/admin/schedules/edit/" + id;
        }

        // Lấy schedule hiện tại và cập nhật
        Schedule schedule = scheduleService.getById(id);
        if (schedule != null) {
            schedule.setMovie(movieService.getMovieById(movieId));
            schedule.setRoom(roomService.getById(roomId));
            schedule.setShowTime(showTimeService.getById(showTimeId));
            schedule.setNgayChieu(ngayChieu);

            boolean saved = scheduleService.save(schedule);
            if (saved) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật lịch chiếu thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật lịch chiếu!");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy lịch chiếu!");
        }

        return "redirect:/admin/schedules";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa lịch chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa lịch chiếu!");
        }
        return "redirect:/admin/schedules";
    }

    @PostMapping("/status/{id}")
    public String changeStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Schedule s = scheduleService.getById(id);
        if (s != null) {
            int newStatus = (s.getStatus() == 1 ? 0 : 1);
            scheduleService.changeStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("success", "Thay đổi trạng thái thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy lịch chiếu!");
        }
        return "redirect:/admin/schedules";
    }
}