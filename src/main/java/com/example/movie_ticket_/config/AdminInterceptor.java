package com.example.movie_ticket_.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Chặn mọi trang /admin/** nếu chưa đăng nhập bằng tài khoản admin.
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null
                && session.getAttribute(AdminSessionUser.SESSION_KEY) instanceof AdminSessionUser;

        if (loggedIn) {
            // Không cho trình duyệt cache trang admin -> bấm Back sau khi đăng xuất sẽ không thấy dữ liệu
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            return true;
        }

        String loginUrl = request.getContextPath() + "/admin/login";
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String target = request.getRequestURI().substring(request.getContextPath().length());
            if (request.getQueryString() != null) {
                target += "?" + request.getQueryString();
            }
            loginUrl += "?redirect=" + URLEncoder.encode(target, StandardCharsets.UTF_8);
        }
        response.sendRedirect(loginUrl);
        return false;
    }
}
