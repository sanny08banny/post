package com.example.sammwangi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class NotificationItemAdapter extends RecyclerView.Adapter<NotificationItemAdapter.HeaderViewHolder> {
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ITEM = 2;
    private final Map<String,List<NotificationsItem>> notificationsItems;
    private Context context;

    public NotificationItemAdapter(Map<String,List<NotificationsItem>> notificationItems, Context context) {
        this.notificationsItems = notificationItems;
        this.context = context;
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header,parent,false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        List<String> dateList = new ArrayList<>(notificationsItems.keySet());
        String date = dateList.get(position);
        List<NotificationsItem> notificationsItemList = notificationsItems.get(date);
        assert notificationsItemList != null;
        Log.e("NotificationsItemAdapter","Number of items in notificationItemList: " + notificationsItemList.size());
//        Date date2 = null;
//        try {
//            date2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(date);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
//        assert date2 != null;
//        String formattedDate = simpleDateFormat.format(date2);

        holder.headerTextView.setText(date);

        Collections.sort(notificationsItemList, new Comparator<NotificationsItem>() {
            @Override
            public int compare(NotificationsItem o1, NotificationsItem o2) {
                return o2.getDateReceived().compareTo(o1.getDateReceived());
            }
        });

        InnerRecyclerViewAdapter innerRecyclerViewAdapter = new InnerRecyclerViewAdapter(notificationsItemList);
        holder.innerRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.innerRecycler.setAdapter(innerRecyclerViewAdapter);
    }

    @Override
    public int getItemCount() {
        Log.e("NotificationsItemAdapter","Number of items: " + notificationsItems.size());

        return notificationsItems.size();
    }


    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerTextView;
        private final RecyclerView innerRecycler;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.headerTextView);
            innerRecycler = itemView.findViewById(R.id.innerRecycler);

//            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
//            innerRecycler.setLayoutManager(layoutManager);

        }

    }
}
