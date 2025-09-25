package com.example.movie_ticket_.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRandomPassword(String toEmail, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mật khẩu đăng nhập lần đầu");
        message.setText("Chào bạn, mật khẩu đăng nhập lần đầu của bạn là: " + password +
                                "\nVui lòng đổi mật khẩu sau khi đăng nhập.");
        mailSender.send(message);
    }
}
