package com.example.sammwangi.adapters;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.DAOs.ProfileDTO;
import com.example.sammwangi.R;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private  List<PostItem> postItemList;

    public PostAdapter(List<PostItem> postItemList) {
//        if (this.postItemList == null){
//            this.postItemList = new ArrayList<>();
//        }else {
        Collections.sort(postItemList, new Comparator<PostItem>() {
            @Override
            public int compare(PostItem o1, PostItem o2) {
                return o2.getDateTime().compareTo(o1.getDateTime());
            }
        });
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

        double amount = postItem.getPostAmount();
        Locale kenyanLocale = new Locale("sw","KE");
        Currency kenyanShilling = Currency.getInstance("KES");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
        numberFormat.setCurrency(kenyanShilling);
        String formattedAmount = numberFormat.format(amount);

        holder.amount.setText(formattedAmount);
        holder.transactionId.setText(postItem.getTransactionId());
        holder.transactionType.setText(postItem.getTransactionType());
        holder.postType.setText(postItem.getPostType());
        ProfileDTO profileDTO = postItem.getProfileDTO();
        if (profileDTO != null) {
            holder.name.setText(profileDTO.getFullName());
        }
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
                    boolean isExpanded = relativeLayout.getVisibility() == View.VISIBLE;
                    toggleExpansion(relativeLayout,relativeLayout2, isExpanded,moreDetailsButton);
                }
            });

        }
        private void toggleExpansion(View expandLayout,View expandLayout2, boolean isExpanded,ImageButton imageButton) {
            if (isExpanded) {
                expandLayout.setVisibility(View.GONE);
                expandLayout2.setVisibility(View.GONE);
                imageButton.setImageResource(R.drawable.baseline_keyboard_arrow_down_32);
                ObjectAnimator.ofFloat(expandLayout, "scaleY", 1f, 0f)
                        .setDuration(300)
                        .start();
            } else {
                expandLayout.setVisibility(View.VISIBLE);
                expandLayout2.setVisibility(View.VISIBLE);
                imageButton.setImageResource(R.drawable.baseline_keyboard_arrow_up_32);
                ObjectAnimator.ofFloat(expandLayout, "scaleY", 0f, 1f)
                        .setDuration(300)
                        .start();
            }
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
