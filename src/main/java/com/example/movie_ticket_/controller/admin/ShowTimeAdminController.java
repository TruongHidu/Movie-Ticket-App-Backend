package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.entity.ShowTime;
import com.example.movie_ticket_.service.ShowTimeService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@Controller
@RequestMapping("/admin/showtimes")
@RequiredArgsConstructor
public class ShowTimeAdminController {

    private final ShowTimeService showTimeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("showtimes", showTimeService.getAll());
        return "showtime/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("showtime", new ShowTime());
        return "showtime/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute ShowTime showTime) {
        showTimeService.save(showTime);
        return "redirect:/admin/showtimes";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        ShowTime st = showTimeService.getById(id);
        if (st == null)
            return "redirect:/admin/showtimes";
        model.addAttribute("showtime", st);
        return "showtime/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
            @RequestParam("khungGio") String khungGioStr) {
        ShowTime st = showTimeService.getById(id);
        if (st != null) {
            st.setKhungGio(LocalTime.parse(khungGioStr)); // ✅ parse "09:00" → LocalTime
            showTimeService.save(st);
        }
        return "redirect:/admin/showtimes";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        showTimeService.delete(id);
        return "redirect:/admin/showtimes";
    }

    @PostMapping("/status/{id}")
    public String changeStatus(@PathVariable Long id) {
        ShowTime showtime = showTimeService.getById(id);
        if (showtime != null) {
            int newStatus = showtime.getStatus() == 1 ? 0 : 1;
            showTimeService.changeStatus(id, newStatus);
        }
        return "redirect:/admin/showtimes";
    }
}
