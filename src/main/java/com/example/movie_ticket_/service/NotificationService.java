package com.example.movie_ticket_.service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

//    public void sendPromotionNotification(String title, String body, String imageUrl)
//            throws FirebaseMessagingException {
//
//        Notification notification = Notification.builder()
//                .setTitle(title)
//                .setBody(body)
//                .setImage(imageUrl)
//                .build();
//
//        AndroidConfig androidConfig = AndroidConfig.builder()
//                .setPriority(AndroidConfig.Priority.HIGH)
//                .setNotification(
//                        AndroidNotification.builder()
//                                .setImage(imageUrl)
//                                .setSound("default")
//                                .build()
//                )
//                .build();
//
//        Message message = Message.builder()
//                .setTopic("promotions")
//                .setNotification(notification) // 🔥 để Android tự hiển thị
//                .setAndroidConfig(androidConfig)
//
//                // 🔥 DATA PAYLOAD (QUAN TRỌNG)
//                .putData("type", "PROMOTION")
//                .putData("title", title)
//                .putData("body", body)
//                .putData("imageUrl", imageUrl)
//
//                .build();
//
//        FirebaseMessaging.getInstance().send(message);
//    }

}
