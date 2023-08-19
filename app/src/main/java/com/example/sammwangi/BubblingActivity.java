package com.example.sammwangi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.graphics.drawable.IconCompat;

import com.example.sammwangi.activities.MainActivity;

public class BubblingActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "bubbling_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubbling);

        // Show the bubble when the activity is created
        showBubble();
    }

    private void showBubble() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (areBubblesAllowed()) {
                Notification notification = createBubbleNotification();
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
                NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
            }
        }
    }

    private boolean areBubblesAllowed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            return notificationManager.areBubblesAllowed();
        }
        return true; // Bubbles are allowed by default on older Android versions
    }

    private Notification createBubbleNotification() {
        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        IconCompat bubbleIcon = IconCompat.createWithResource(this, R.drawable.admin_01);

        NotificationCompat.BubbleMetadata bubbleMetadata = new NotificationCompat.BubbleMetadata.Builder(contentIntent, bubbleIcon)
                .setDesiredHeight(600)
                .setAutoExpandBubble(true)
                .setSuppressNotification(true)
                .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Bubbling Notification")
                .setContentText("This is a bubbling notification")
                .setSmallIcon(R.drawable.admin_01)
                .setAutoCancel(true)
                .setBubbleMetadata(bubbleMetadata);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = getString(R.string.channel_name);
            String channelDescription = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
