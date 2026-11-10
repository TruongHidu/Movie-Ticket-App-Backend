package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.config.AdminLoginAttemptService;
import com.example.movie_ticket_.config.AdminSessionUser;
import com.example.movie_ticket_.entity.User;
import com.example.movie_ticket_.repository.UserRepository;
import com.example.movie_ticket_.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    private static final String INVALID_CREDENTIALS = "Email hoặc mật khẩu không chính xác.";

    private final UserRepository userRepository;
    private final UserService userService;
    private final AdminLoginAttemptService attemptService;

    @GetMapping("/")
    public String root(HttpSession session) {
        return isLoggedIn(session) ? "redirect:/admin" : "redirect:/admin/login";
    }

    @GetMapping("/admin/login")
    public String showLoginForm(@RequestParam(value = "redirect", required = false) String redirect,
                                @RequestParam(value = "logout", required = false) String logout,
                                HttpSession session,
                                Model model) {
        if (isLoggedIn(session)) {
            return "redirect:" + safeRedirect(redirect);
        }
        if (logout != null) {
            model.addAttribute("info", "Bạn đã đăng xuất khỏi trang quản trị.");
        } else if (redirect != null) {
            model.addAttribute("info", "Vui lòng đăng nhập để tiếp tục.");
        }
        model.addAttribute("redirect", safeRedirect(redirect));
        return "login";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        @RequestParam(value = "redirect", required = false) String redirect,
                        HttpServletRequest request,
                        Model model) {
        String ip = request.getRemoteAddr();
        String normalizedEmail = email == null ? "" : email.trim();
        model.addAttribute("email", normalizedEmail);
        model.addAttribute("redirect", safeRedirect(redirect));

        long lockedMinutes = attemptService.minutesLocked(ip, normalizedEmail);
        if (lockedMinutes > 0) {
            model.addAttribute("error", "Bạn đã nhập sai quá nhiều lần. Vui lòng thử lại sau " + lockedMinutes + " phút.");
            return "login";
        }

        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
        boolean passwordOk = userOpt.isPresent()
                && userOpt.get().getPassword() != null
                && userService.matchesPassword(password, userOpt.get().getPassword());

        if (!passwordOk) {
            int left = attemptService.recordFailure(ip, normalizedEmail);
            model.addAttribute("error", left > 0
                    ? INVALID_CREDENTIALS + " Còn " + left + " lần thử."
                    : "Bạn đã nhập sai quá nhiều lần. Vui lòng thử lại sau "
                        + AdminLoginAttemptService.LOCK_DURATION.toMinutes() + " phút.");
            return "login";
        }

        User user = userOpt.get();
        if (user.getRole() != 1) {
            model.addAttribute("error", "Tài khoản này không có quyền truy cập trang quản trị.");
            return "login";
        }
        if (user.getStatus() == 0) {
            model.addAttribute("error", "Tài khoản quản trị đang bị khóa.");
            return "login";
        }

        attemptService.recordSuccess(ip, normalizedEmail);

        // Chống session fixation: tạo session mới rồi đổi ID trước khi lưu thông tin đăng nhập
        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute(AdminSessionUser.SESSION_KEY, AdminSessionUser.from(user));
        session.setMaxInactiveInterval(60 * 60); // 1 giờ không thao tác thì phải đăng nhập lại

        return "redirect:" + safeRedirect(redirect);
    }

    @PostMapping("/admin/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/admin/login?logout";
    }

    /** Giữ tương thích với link cũ. */
    @GetMapping("/logout-admin")
    public String legacyLogout(HttpServletRequest request) {
        return logout(request);
    }

    private static boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute(AdminSessionUser.SESSION_KEY) instanceof AdminSessionUser;
    }

    /** Chỉ cho phép quay lại các đường dẫn nội bộ trong /admin (tránh open redirect). */
    static String safeRedirect(String target) {
        if (target == null || target.isBlank()) return "/admin";
        boolean internal = (target.equals("/admin") || target.startsWith("/admin/") || target.startsWith("/admin?"))
                && !target.startsWith("/admin/login")
                && !target.contains("\\")
                && !target.contains("\r") && !target.contains("\n")
                && !target.contains("//");
        return internal ? target : "/admin";
    }
}
