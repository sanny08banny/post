package com.example.sammwangi.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.sammwangi.DAOs.ProfileDTO;
import com.example.sammwangi.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<ProfileDTO> userList;
    private Context context;

    public UserAdapter(@NonNull Context context, List<ProfileDTO> userList) {
        this.userList = userList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ProfileDTO user = userList.get(position);

        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setItems(List<ProfileDTO> data) {
        if (data != null) {
            userList = data;
        }
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView profileImage;
        TextView profileNameTextView;
        TextView profileUserNameTextView;
        TextView profileFollowersTextView;
        ImageButton removeFromListButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileNameTextView = itemView.findViewById(R.id.profile_name);
            profileUserNameTextView = itemView.findViewById(R.id.profile_user_name);
            profileFollowersTextView = itemView.findViewById(R.id.profile_followers);
            removeFromListButton = itemView.findViewById(R.id.remove_from_list);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void bind(ProfileDTO profileDTO) {
            Glide.with(context).asBitmap().load(profileDTO.getFilePath())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_baseline_account_box_32) // Placeholder image while loading
                            .error(R.drawable.outline_error_outline_48)      // Error image if loading fails
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .override(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Set the loaded bitmap to the ImageView
                            profileImage.setImageBitmap(resource);

                            // Retain the original aspect ratio of the image
                            float aspectRatio = (float) resource.getWidth() / resource.getHeight();

                            // Calculate the desired height based on the original aspect ratio
                            int desiredHeight = (int) (profileImage.getWidth() / aspectRatio);

                            // Resize the ImageView to the desired height while keeping the width MATCH_PARENT
                            ViewGroup.LayoutParams layoutParams = profileImage.getLayoutParams();
                            layoutParams.height = desiredHeight;
                            profileImage.setLayoutParams(layoutParams);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Clear any previous loaded resources if needed
                        }
                    });
            profileNameTextView.setText(profileDTO.getUserName());
            profileUserNameTextView.setText(profileDTO.getUserName());

            removeFromListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}

