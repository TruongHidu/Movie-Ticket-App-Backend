// AdminRoomController.java
package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.entity.Room;
import com.example.movie_ticket_.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
public class RoomAdminController {

    private final RoomService roomService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rooms", roomService.getAll());
        return "room/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("room", new Room());
        return "room/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Room room) {
        System.out.println("==> ID nhận từ form: " + room.getIdPhongChieu());
        roomService.add(room);
        return "redirect:/admin/rooms";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        Room room = roomService.getById(id);
        if (room == null) {
            return "redirect:/admin/rooms";
        }
        model.addAttribute("room", room);
        return "room/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable String id, @RequestParam("tenPhongChieu") String tenPhongChieu) {
        Room room = roomService.getById(id);
        if (room != null) {
            room.setTenPhongChieu(tenPhongChieu);
            roomService.save(room);
        }
        return "redirect:/admin/rooms";
    }

    @PostMapping("/status/{id}")
    public String changeStatus(@PathVariable String id) {
        Room room = roomService.getById(id);
        if (room != null) {
            int newStatus = room.getStatus() == 1 ? 0 : 1;
            roomService.changeRoomStatus(id, newStatus);
        }
        return "redirect:/admin/rooms";
    }

}
