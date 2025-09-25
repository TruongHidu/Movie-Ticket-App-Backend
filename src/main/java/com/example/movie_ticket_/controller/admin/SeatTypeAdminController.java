package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.entity.SeatType;
import com.example.movie_ticket_.service.SeatTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/seat-types")
@RequiredArgsConstructor
public class SeatTypeAdminController {

    private final SeatTypeService seatTypeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("seatTypes", seatTypeService.getAll());
        return "seattype/seat-types"; // view danh sách
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("seatType", new SeatType());
        return "seattype/add-seat-type";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute SeatType seatType) {
        seatTypeService.save(seatType);
        return "redirect:/admin/seat-types";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        model.addAttribute("seatType", seatTypeService.getById(id));
        return "seattype/edit-seat-type";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable String id, @ModelAttribute SeatType seatType) {
        seatType.setIdLoaiGhe(id);
        seatTypeService.save(seatType);
        return "redirect:/admin/seat-types";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        seatTypeService.delete(id);
        return "redirect:/admin/seat-types";
    }
}
