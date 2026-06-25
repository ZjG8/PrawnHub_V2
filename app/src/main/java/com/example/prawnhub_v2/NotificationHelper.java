package com.example.prawnhub_v2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

public final class NotificationHelper {
    private static final String CHANNEL_NAME = "ShrimpHub Alerts";

    private NotificationHelper() {
    }

    public static void showAlert(Context context, String title, String message, int id) {
        if (!NotificationSettings.pushEnabled(context)) {
            return;
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId(context),
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            if (!NotificationSettings.soundEnabled(context)) {
                channel.setSound(null, null);
            } else {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, attributes);
            }
            channel.enableVibration(NotificationSettings.vibrateEnabled(context));
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId(context))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        if (!NotificationSettings.soundEnabled(context)) {
            builder.setSilent(true);
        }
        if (NotificationSettings.vibrateEnabled(context)) {
            builder.setVibrate(new long[]{0L, 300L, 180L, 300L});
        } else {
            builder.setVibrate(new long[]{0L});
        }

        manager.notify(id, builder.build());
    }

    private static String channelId(Context context) {
        return "shrimphub_alerts_sound_"
                + NotificationSettings.soundEnabled(context)
                + "_vibrate_"
                + NotificationSettings.vibrateEnabled(context);
    }
}
