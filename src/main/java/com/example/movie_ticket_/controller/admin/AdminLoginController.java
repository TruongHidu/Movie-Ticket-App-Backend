package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.entity.User;
import com.example.movie_ticket_.repository.UserRepository;
import com.example.movie_ticket_.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AdminLoginController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/")
    public String showLoginForm(HttpSession session) {
        User adminUser = (User) session.getAttribute("adminUser");
        if (adminUser != null && adminUser.getRole() == 1) {
            return "redirect:/admin";
        }
        return "login"; // templates/login.html
    }

    @PostMapping("/login-admin")
    public String loginAdmin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (userService.matchesPassword(password, user.getPassword())) {
                if (user.getRole() == 1) {
                    session.setAttribute("adminUser", user);
                    return "redirect:/admin";
                } else {
                    model.addAttribute("error", "Tài khoản không có quyền quản trị!");
                    return "login";
                }
            } else {
                model.addAttribute("error", "Mật khẩu không chính xác!");
                return "login";
            }
        } else {
            model.addAttribute("error", "Tài khoản không tồn tại!");
            return "login";
        }
    }

    @GetMapping("/logout-admin")
    public String logoutAdmin(HttpSession session) {
        session.removeAttribute("adminUser");
        session.invalidate();
        return "redirect:/";
    }
}
