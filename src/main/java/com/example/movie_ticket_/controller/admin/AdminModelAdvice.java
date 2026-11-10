package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.config.AdminSessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Đưa thông tin admin đang đăng nhập vào model của mọi trang quản trị (dùng cho sidebar/topbar).
 */
@ControllerAdvice(basePackages = "com.example.movie_ticket_.controller.admin")
public class AdminModelAdvice {

    @ModelAttribute("currentAdmin")
    public AdminSessionUser currentAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object value = session.getAttribute(AdminSessionUser.SESSION_KEY);
        return value instanceof AdminSessionUser admin ? admin : null;
    }
}
