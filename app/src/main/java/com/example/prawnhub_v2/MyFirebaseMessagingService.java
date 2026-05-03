package com.example.prawnhub_v2;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        String title = "ShrimpHub Alert";
        String body = "Tank alert received.";

        if (message.getNotification() != null) {
            if (message.getNotification().getTitle() != null) {
                title = message.getNotification().getTitle();
            }
            if (message.getNotification().getBody() != null) {
                body = message.getNotification().getBody();
            }
        }

        String alertType = message.getData().get("alert_type");
        if ("ammonia_risk".equals(alertType)) {
            body = "ShrimpHub: High Ammonia Risk! Filter activated.";
        } else if ("overflow_risk".equals(alertType)) {
            body = "ShrimpHub: Water level low! Pump activated.";
        } else if ("temp_alert".equals(alertType)) {
            body = "ShrimpHub: Temperature out of safe range!";
        }

        NotificationHelper.showAlert(this, title, body, 9001);
    }
}
