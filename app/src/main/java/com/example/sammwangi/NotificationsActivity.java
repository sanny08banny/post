package com.example.sammwangi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class NotificationsActivity extends AppCompatActivity {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private NotificationItemAdapter adapter;
    private LinearLayoutManager layoutManager;

    private List<NotificationsItem> notificationsItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notifications");


        recyclerView = findViewById(R.id.notifications_recycler);

        // Retrieve notifications from the database
        NotificationsDbHelper dbHelper = new NotificationsDbHelper(NotificationsActivity.this);
        notificationsItems = dbHelper.getAllNotifications();
        Log.d("Notification Activity", "Number of items in the notificationItems: " + notificationsItems.size());

        Map<String, List<NotificationsItem>> groupedItems = new TreeMap<>(Collections.reverseOrder());

        for (NotificationsItem notificationsItem : notificationsItems) {
            if (notificationsItem.getDateReceived() != null) {
                String date = notificationsItem.getDateReceived();
                Date date2 = null;
                try {
                    date2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                assert date2 != null;
                String formattedDate = simpleDateFormat.format(date2);

                // Check if the date is already present in the map
                if (groupedItems.containsKey(formattedDate)) {
                    // Date exists, retrieve the existing list of items and add the current item to it
                    List<NotificationsItem> itemsForDate = groupedItems.get(formattedDate);
                    itemsForDate.add(notificationsItem);
                } else {
                    // Date does not exist, create a new list and add the current item to it
                    List<NotificationsItem> itemsForDate = new ArrayList<>();
                    itemsForDate.add(notificationsItem);
                    groupedItems.put(formattedDate, itemsForDate);
                }
            }
        }

        Map<String, List<NotificationsItem>> sortedItems = new TreeMap<>(groupedItems);

// Iterate over the sorted map
        for (Map.Entry<String, List<NotificationsItem>> entry : sortedItems.entrySet()) {
            String formattedDate = entry.getKey();
            List<NotificationsItem> itemsForDate = entry.getValue();
            // Assuming you have an instance of RecyclerView called 'recyclerView' in your activity/fragment
            adapter = new NotificationItemAdapter(sortedItems, this);
        }

// Pass the groupedItems map to the adapter
//        adapter.setPosts(notificationsItems);

// Set the adapter on the RecyclerView
        recyclerView.setAdapter(adapter);

// Set the layout manager for the RecyclerView (e.g., LinearLayoutManager)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }
}