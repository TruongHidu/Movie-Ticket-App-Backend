package com.example.movie_ticket_.config;

import com.example.movie_ticket_.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Thông tin admin lưu trong HttpSession sau khi đăng nhập.
 * Chỉ giữ những trường cần hiển thị — không lưu entity User (có mật khẩu đã băm) vào session.
 */
@Getter
@AllArgsConstructor
public class AdminSessionUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String SESSION_KEY = "adminUser";

    private final Long id;
    private final String name;
    private final String email;

    public static AdminSessionUser from(User user) {
        String name = (user.getName() == null || user.getName().isBlank()) ? user.getEmail() : user.getName();
        return new AdminSessionUser(user.getId(), name, user.getEmail());
    }

    /** Chữ cái đầu dùng cho avatar, ví dụ "Văn Trường" -> "VT". */
    public String initials() {
        if (name == null || name.isBlank()) return "A";
        String[] parts = name.trim().split("\\s+");
        String first = parts[0].substring(0, 1);
        String last = parts.length > 1 ? parts[parts.length - 1].substring(0, 1) : "";
        return (first + last).toUpperCase();
    }
}
