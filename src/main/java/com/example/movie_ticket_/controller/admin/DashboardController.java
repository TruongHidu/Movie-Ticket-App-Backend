package com.example.movie_ticket_.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/admin")
    public String dashboard(Model model) {
        // Bạn có thể truyền thêm thống kê, số lượng phim, số lượng user ...
        model.addAttribute("title", "Admin Dashboard");
        return "dashboard"; // => templates/dashboard.html
    }
}
