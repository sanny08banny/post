package com.example.sammwangi;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private  List<PostItem> postItemList;

    public PostAdapter(List<PostItem> postItemList) {
//        if (this.postItemList == null){
//            this.postItemList = new ArrayList<>();
//        }else {
            this.postItemList = postItemList;

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostItem postItem = postItemList.get(position);
        holder.date.setText(postItem.getDatePosted());

        double amount = Double.parseDouble(postItem.getPostAmount());
        Locale kenyanLocale = new Locale("sw","KE");
        Currency kenyanShilling = Currency.getInstance("KES");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
        numberFormat.setCurrency(kenyanShilling);
        String formattedAmount = numberFormat.format(amount);

        holder.amount.setText(formattedAmount);
        holder.transactionId.setText(postItem.getTransactionId());
        holder.transactionType.setText(postItem.getTransactionType());
        holder.postType.setText(postItem.getPostType());
        holder.name.setText(postItem.getFullName());
    }

    @Override
    public int getItemCount() {
//        Log.d("ProfileAdapter","Number of items in the postItemList: " + postItemList.size());
        return postItemList.size();
    }
    public void updatePosts(List<PostItem>posts){
        postItemList = posts;
        notifyDataSetChanged();
    }

    public void addItem(PostItem postItem) {
        postItemList.add(0,postItem);
        notifyItemInserted(0);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView amount;
        private final TextView name;
        private final TextView postType;
        private final TextView date;
        private final TextView transactionType;
        private final TextView transactionId;
        private final RelativeLayout relativeLayout;
        private final RelativeLayout relativeLayout2;
        private final ImageButton moreDetailsButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_of_poster);
            date = itemView.findViewById(R.id.date_posted);
            amount = itemView.findViewById(R.id.amount_posted);
            transactionId = itemView.findViewById(R.id.post_transaction_id);
            transactionType = itemView.findViewById(R.id.post_transaction_type);
            postType = itemView.findViewById(R.id.post_type);
            relativeLayout = itemView.findViewById(R.id.more_post_details_layout);
            relativeLayout2 = itemView.findViewById(R.id.post_title_layout);
            moreDetailsButton = itemView.findViewById(R.id.more_post_details_button);
            moreDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (relativeLayout.getVisibility() == View.GONE && relativeLayout2.getVisibility() == View.GONE){
                        relativeLayout.setVisibility(View.VISIBLE);
                        relativeLayout2.setVisibility(View.VISIBLE);
                    }else {
                        relativeLayout.setVisibility(View.GONE);
                        relativeLayout2.setVisibility(View.GONE);
                    }
                }
            });

        }
//        public void bind(PostItem postItem){
////            name.setText(postItem.getContribution().compareTo(BigDecimal.)>0?"Contribution":"New Shares");
//            date.setText(postItem.getDatePosted());
////            reference.setText(postItem.getReferenceNumber());
//            amount.setText(postItem.getPostAmount());
//        }

//        @Override
//        public void onClick(View v) {
////            Intent intent = new Intent(v.getContext(),GeneralPostTrace.class);
////            v.getContext().startActivity(intent);
//        }
    }
}
