package com.example.sammwangi;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminPostsAdapterInner extends RecyclerView.Adapter<AdminPostsAdapterInner.AdminPostViewHolder> {
    private List<PostItem> myPostItemList;

    public AdminPostsAdapterInner(List<PostItem> myPostItemList) {
        this.myPostItemList = myPostItemList;
    }

    public void updatePosts(List<PostItem>posts){
        myPostItemList = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_layout,parent,false);
        return new AdminPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPostViewHolder holder, int position) {
        PostItem postItem = myPostItemList.get(position);

        if (postItem.getTransactionType().equals("Direct Banking")){
            holder.imageView.setImageResource(R.drawable.direct_banking_01);
        }else if (postItem.getTransactionType().equals("M-PESA")){
            holder.imageView.setImageResource(R.drawable.mpesa_icon_01);
        }
//        holder.transactionId.setText(postItem.getTransactionId());
        holder.transactionType.setText(postItem.getPostType());

        double amount = Double.parseDouble(postItem.getPostAmount());
        Locale kenyanLocale = new Locale("sw","KE");
        Currency kenyanShilling = Currency.getInstance("KES");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
        numberFormat.setCurrency(kenyanShilling);
        String formattedAmount = numberFormat.format(amount);

        holder.amountPosted.setText(MessageFormat.format(":  {0}", formattedAmount));

        String date = postItem.getDateTime();
        Date date2 = null;
        try {
            date2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        assert date2 != null;

        String formattedDate = simpleDateFormat.format(date2);

        holder.postAge.setText(formattedDate);

        holder.transactionId.setText(postItem.getTransactionId());
    }

    @Override
    public int getItemCount() {
        Log.d("Retrieved post","Number of items in myPostItemList: " + myPostItemList.size());

        return myPostItemList.size();
    }

    public List<PostItem> getPostItems() {
        return myPostItemList;
    }

    public static class AdminPostViewHolder extends RecyclerView.ViewHolder{
        private final TextView postAge;
        private final TextView transactionType;
        private final TextView transactionId;
        private final TextView amountPosted;

        private final ShapeableImageView imageView;

        public AdminPostViewHolder(@NonNull View itemView) {
            super(itemView);
//            transactionId = itemView.findViewById(R.id.myPost_transaction_id);
            transactionType = itemView.findViewById(R.id.admin_post_transaction_type);
            transactionId = itemView.findViewById(R.id.admin_transaction_id);
            amountPosted = itemView.findViewById(R.id.admin_post_amount);
            postAge  = itemView.findViewById(R.id.admin_post_age);
            imageView = itemView.findViewById(R.id.transaction_type_image_admin);
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
