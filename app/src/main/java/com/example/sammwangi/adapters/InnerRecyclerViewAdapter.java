package com.example.sammwangi.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sammwangi.NotificationsItem;
import com.example.sammwangi.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InnerRecyclerViewAdapter extends RecyclerView.Adapter<InnerRecyclerViewAdapter.ItemViewHolder> {
    private final List<NotificationsItem> notificationsItems2;

    public InnerRecyclerViewAdapter(List<NotificationsItem> notificationsItems2) {
        this.notificationsItems2 = notificationsItems2;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        NotificationsItem notificationsItem = notificationsItems2.get(position);
        holder.bind(notificationsItem);
    }

    @Override
    public int getItemCount() {
        Log.e("Inner Recycler Adapter", "Number of inner items: " + notificationsItems2.size());

        return notificationsItems2.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView body;
        private final TextView ageDate;
        private final ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            body = itemView.findViewById(R.id.notification_body);
            ageDate = itemView.findViewById(R.id.notification_time_duration);
            imageView = itemView.findViewById(R.id.notification_item_image);
        }

        void bind(NotificationsItem item) {
            title.setText(item.getTitle());
            body.setText(item.getBody());
            ageDate.setText(getTimeDifference(item.getDateReceived()));
            Log.e("Inner Recycler Adapter", "Notification type: " + item.getNotificationType());

            if (item.getNotificationType() != null && item.getNotificationType().matches("Administrative")) {
                imageView.setImageResource(R.drawable.admin_01);
            } else if (item.getNotificationType() == null) {
                imageView.setImageResource(item.getNotificationImage());
            }
        }
    }

    private String getTimeDifference(String datePosted) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
            Date postedDate = dateFormat.parse(datePosted);
            Date currentDate = new Date();
            assert postedDate != null;
            long diffInMilliseconds = currentDate.getTime() - postedDate.getTime();

            // Calculate the difference in seconds, minutes, hours, or days
            long seconds = diffInMilliseconds / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) {
                return days + (days == 1 ? " day ago" : " days ago");
            } else if (hours > 0) {
                return hours + (hours == 1 ? " hour ago" : " hours ago");
            } else if (minutes > 0) {
                return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
            } else {
                return seconds + (seconds == 1 ? " second ago" : " seconds ago");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}

