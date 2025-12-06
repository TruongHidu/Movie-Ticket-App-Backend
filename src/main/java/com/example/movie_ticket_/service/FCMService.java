package com.example.movie_ticket_.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    public void sendNotification(String fcmToken, String title, String body) throws FirebaseMessagingException {

//        Notification notification = Notification.builder()
//                .setTitle(title)
//                .setBody(body)
//                .build();
//
//        Message message = Message.builder()
//                .setToken(fcmToken)
//                .setNotification(notification)
//                .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
//                .build();

        Message message = Message.builder()
                .setToken(fcmToken)
                .putData("title", title)
                .putData("body", body)
                .putData("imageUrl", "")
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .build();


        FirebaseMessaging.getInstance().send(message);
    }
}
