package com.example.sammwangi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AdminPostAdapter extends RecyclerView.Adapter<AdminPostAdapter.HeaderViewHolder> {
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ITEM = 2;
    private final Map<String, List<PostItem>> groupedPostList;
    private Context context;

    public AdminPostAdapter(Map<String,List<PostItem>> groupedPostList, Context context) {
        this.groupedPostList = groupedPostList;
        this.context = context;
    }
//    public void updatePosts(List<PostItem>posts){
//        groupedPostList = posts;
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header,parent,false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        List<String> dateList = new ArrayList<>(groupedPostList.keySet());
        String date = dateList.get(position);
        List<PostItem> notificationsItemList = groupedPostList.get(date);
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

        Collections.sort(notificationsItemList, new Comparator<PostItem>() {
            @Override
            public int compare(PostItem o1, PostItem o2) {
                return o2.getDateTime().compareTo(o1.getDateTime());
            }
        });

        AdminPostsAdapterInner adminPostsAdapterInner = new AdminPostsAdapterInner(notificationsItemList, context);
        holder.innerRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.innerRecycler.setAdapter(adminPostsAdapterInner);
    }

    @Override
    public int getItemCount() {
        return groupedPostList.size();
    }

    public Map<String, List<PostItem>> getGroupedItems() {
        return groupedPostList;
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

