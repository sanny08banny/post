package com.example.sammwangi;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        FCMTokenManager.saveToken(getApplicationContext(), token);
        Log.d("FCM Token", token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String imageUrlType = String.valueOf(remoteMessage.getNotification().getImageUrl());

        // Check if notifications are enabled
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        boolean bubblingNotificationsEnabled = sharedPreferences.getBoolean("bubbling_notifications_enabled", false);

        if (notificationsEnabled) {
            // Save the notification to the database
            Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.admin_01);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = simpleDateFormat.format(new Date());
            String dateTime = String.valueOf(new Date());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            String formattedTime = sdf.format(new Date());
            String dateReceived = String.valueOf(new Date());

            // Save the notification to the database
            NotificationsDbHelper dbHelper = new NotificationsDbHelper(this);
            NotificationsItem notification = new NotificationsItem();
            notification.setTitle(title);
            notification.setBody(body);
            notification.setTimeReceived(formattedTime);
            notification.setDateReceived(dateReceived);
            notification.setNotificationType(imageUrlType);
            boolean notificationSavedLocally = dbHelper.addNotification(notification);

            // Create the notification intent
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            // Build the notification
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "sam_mwangi")
                    .setSmallIcon(R.drawable.baseline_link_24)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            int notificationId = (int) (System.currentTimeMillis() / 1000);
            notificationManager.notify(notificationId, builder.build());
        }

        if (bubblingNotificationsEnabled) {
            // Create a new activity intent for the bubbling notification
            Intent bubblingIntent = new Intent(this, BubblingActivity.class);
            bubblingIntent.putExtra("title", title);
            bubblingIntent.putExtra("body", body);

            // Set the bubbling notification flags
            bubblingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, bubblingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create the bubbling notification
            NotificationCompat.Builder bubblingBuilder = new NotificationCompat.Builder(this, "sam_mwangi")
                    .setSmallIcon(R.drawable.baseline_link_24)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setAutoCancel(true)
                    .setFullScreenIntent(pendingIntent, true);

            int notificationId = (int) (System.currentTimeMillis() / 1000);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            NotificationManagerCompat.from(this).notify(notificationId, bubblingBuilder.build());
        }

        super.onMessageReceived(remoteMessage);
    }
}

