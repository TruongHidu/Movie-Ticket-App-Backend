package com.example.movie_ticket_.config;

import com.example.movie_ticket_.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User adminUser = (User) session.getAttribute("adminUser");

        if (adminUser == null || adminUser.getRole() != 1) {
            response.sendRedirect("/");
            return false;
        }
        return true;
    }
}
