package com.example.sammwangi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.example.sammwangi.DAOs.ActivityItem;
import com.example.sammwangi.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityListAdapter extends ArrayAdapter<ActivityItem> {
    private final Context context;
    private final ArrayList<ActivityItem> activityList;

    public ActivityListAdapter(Context context, ArrayList<ActivityItem> activityList) {
        super(context, R.layout.event_activity_item, activityList);
        this.context = context;
        this.activityList = activityList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_activity_item, parent, false);
        }

        ActivityItem activityItem = activityList.get(position);

        TextView titleTextView = convertView.findViewById(R.id.activity_title);
        TextView descTextView = convertView.findViewById(R.id.activity_desc);
        ImageView imageView = convertView.findViewById(R.id.activity_image);
        TextView timestampTextView = convertView.findViewById(R.id.activity_timestamp);
        ImageButton optionsButton = convertView.findViewById(R.id.activity_options);

        titleTextView.setText(activityItem.getTitle());
        descTextView.setText(activityItem.getDescription());
        // Load the image into the ImageView using a library like Glide or Picasso
        // For example, using Glide:
        Glide.with(context)
                .load(activityItem.getImageUrl())
                .into(imageView);
        timestampTextView.setText(activityItem.getTimestamp());

        // Set click listener for the options button
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add functionality to show custom menu options on clicking the options button
                showCustomMenuOptions(activityItem,optionsButton);
            }
        });

        return convertView;
    }

    private void showCustomMenuOptions(ActivityItem activityItem,ImageButton optionsButton) {
        PopupMenu popupMenu = new PopupMenu(context, optionsButton);
        popupMenu.inflate(R.menu.custom_options_menu); // Replace with your custom menu layout if needed

        // Set click listeners for each menu item
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_edit:
                        // Handle the "Edit" menu option
                        // For example, show an edit dialog or navigate to an edit activity
                        return true;
                    case R.id.menu_item_delete:
                        // Handle the "Delete" menu option
                        // For example, show a delete confirmation dialog or perform the deletion
                        return true;
                    // Add more menu options as needed
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    public void setItems(List<ActivityItem> data) {
        activityList.clear();
        activityList.addAll(data);
        notifyDataSetChanged();
    }
}

