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

    public void sendPromotionNotification(String title, String body, String imageUrl)
            throws FirebaseMessagingException {

        // 🔥 Notification payload (để Android tự hiển thị khi background)
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .setImage(imageUrl) // hình ảnh lớn nếu có
                .build();

        // 🔥 Android cấu hình big picture + ưu tiên cao
        AndroidConfig androidConfig = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                                         .setImage(imageUrl)   // hiển thị ảnh lớn BigPicture
                                         .setSound("default")
                                         .build())
                .build();

        // 🔥 Data payload (Flutter có thể đọc để mở màn chi tiết)
        Message message = Message.builder()
                .setTopic("promotions")
                .setNotification(notification)   // MUST HAVE → hiển thị khi tắt màn hình
                .setAndroidConfig(androidConfig)
                .putData("title", title)
                .putData("body", body)
                .putData("imageUrl", imageUrl)
                .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
                .build();

        FirebaseMessaging.getInstance().send(message);
    }
}
