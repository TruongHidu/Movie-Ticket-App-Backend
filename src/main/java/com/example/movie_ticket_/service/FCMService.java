package com.example.movie_ticket_.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    // Trong FCMService.java
    public void sendNotification(String fcmToken, String title, String body, Long invoiceId)
            throws FirebaseMessagingException {

        Message message = Message.builder()
                .setToken(fcmToken)
                .putData("type", "INVOICE_DETAIL")
                .putData("invoiceId", String.valueOf(invoiceId))
                .putData("title", title)
                .putData("body", body)
                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setPriority(AndroidConfig.Priority.HIGH)
                                .build()
                )
                .build();

        FirebaseMessaging.getInstance().send(message);
    }



    public void sendPromotionNotification(
            String fcmToken,
            String title,
            String body,
            String imageUrl
    ) throws FirebaseMessagingException {

        Message message = Message.builder()
                .setToken(fcmToken)

                // 🔥 CHỈ DATA – KHÔNG Notification payload
                .putData("type", "PROMOTION")
                .putData("title", title)
                .putData("body", body)
                .putData("imageUrl", imageUrl)

                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setPriority(AndroidConfig.Priority.HIGH)
                                .build()
                )
                .build();

        FirebaseMessaging.getInstance().send(message);
    }




}