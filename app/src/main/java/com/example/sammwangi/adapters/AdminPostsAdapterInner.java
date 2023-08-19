package com.example.sammwangi.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.DAOs.ProfileDTO;
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

public class AdminPostsAdapterInner extends RecyclerView.Adapter<AdminPostsAdapterInner.AdminPostViewHolder> {
    private List<PostItem> myPostItemList;
    private TextView amount;
    private TextView name;
    private TextView postType;
    private TextView date;
    private TextView transactionTypeSc;
    private TextView transactionIdSc;
    private ImageButton closeWindow;
    private Context context;

    public AdminPostsAdapterInner(List<PostItem> myPostItemList, Context context) {
        this.myPostItemList = myPostItemList;
        this.context = context;
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

        double amount = Double.parseDouble(String.valueOf(postItem.getPostAmount()));
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderTypeDialog(postItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("Retrieved post","Number of items in myPostItemList: " + myPostItemList.size());

        return myPostItemList.size();
    }

    public List<PostItem> getPostItems() {
        return myPostItemList;
    }

    public static class AdminPostViewHolder extends RecyclerView.ViewHolder {
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
            postAge = itemView.findViewById(R.id.admin_post_age);
            imageView = itemView.findViewById(R.id.transaction_type_image_admin);
        }
    }

        private void showOrderTypeDialog(PostItem postItem) {
            final Dialog dialogView = new Dialog(context);
            dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogView.setContentView(R.layout.more_about_transaction);

            name = dialogView.findViewById(R.id.name_of_poster);
            date = dialogView.findViewById(R.id.date_posted);
            amount = dialogView.findViewById(R.id.amount_posted);
            transactionIdSc = dialogView.findViewById(R.id.post_transaction_id);
            transactionTypeSc = dialogView.findViewById(R.id.post_transaction_type);
            postType = dialogView.findViewById(R.id.post_type);

            date.setText(postItem.getDatePosted());

            double amountDouble = postItem.getPostAmount();
            Locale kenyanLocale = new Locale("sw","KE");
            Currency kenyanShilling = Currency.getInstance("KES");
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
            numberFormat.setCurrency(kenyanShilling);
            String formattedAmount = numberFormat.format(amountDouble);

            amount.setText(formattedAmount);
            transactionIdSc.setText(postItem.getTransactionId());
            transactionTypeSc.setText(postItem.getTransactionType());
            postType.setText(postItem.getPostType());
            ProfileDTO profileDTO = postItem.getProfileDTO();
            if (profileDTO != null) {
                name.setText(profileDTO.getFullName());
            }

            closeWindow = dialogView.findViewById(R.id.close_get_reports_window);

            closeWindow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogView.dismiss();
                }
            });

            dialogView.show();
            dialogView.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogView.getWindow().getAttributes().windowAnimations = R.style.OfferDialogAnimation;
            dialogView.getWindow().setGravity(Gravity.CENTER);

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
