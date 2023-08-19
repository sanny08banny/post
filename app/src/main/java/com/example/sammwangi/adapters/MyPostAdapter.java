package com.example.sammwangi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyPostAdapter extends ArrayAdapter<PostItem> {
    public MyPostAdapter(@NonNull Context context, List<PostItem> myPostItems) {
        super(context, R.layout.my_posts_item, myPostItems);
    }
    public void setPostItems(List<PostItem> postItems) {
        clear();
        addAll(postItems);
    }
    public void addPostItem(PostItem postItem) {
        add(postItem);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PostItem postItem = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_posts_item,parent,false);
        }
        ShapeableImageView imageView = convertView.findViewById(R.id.transaction_type_image);
        TextView transactionType = convertView.findViewById(R.id.my_post_transaction_type);
        TextView amountPosted = convertView.findViewById(R.id.my_post_amount);
        TextView age = convertView.findViewById(R.id.my_post_age);
        TextView postType = convertView.findViewById(R.id.my_post_type);

//        imageView.setImageResource(postItem.imageId);
        if (postItem.getTransactionType().equals("Direct Banking")){
            imageView.setImageResource(R.drawable.direct_banking_01);
        }else if (postItem.getTransactionType().equals("M-PESA")){
            imageView.setImageResource(R.drawable.mpesa_icon_01);
        }
        transactionType.setText(postItem.getTransactionType());

        if (postItem.getPostAmount() != null) {
            double amount = postItem.getPostAmount();
            Locale kenyanLocale = new Locale("sw", "KE");
            Currency kenyanShilling = Currency.getInstance("KES");
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
            numberFormat.setCurrency(kenyanShilling);
            String formattedAmount = numberFormat.format(amount);

            amountPosted.setText(MessageFormat.format(":  {0}", formattedAmount));
        }
        age.setText(getTimeDifference(postItem.getDateTime()));

        postType.setText(postItem.getPostType());

        return convertView;
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
