package com.example.sammwangi.adapters;


import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.database_helpers.DataBaseHelper;
import com.example.sammwangi.R;
import com.example.sammwangi.activities.ViewProfileActivity;

import java.util.List;

public class SettingProfileAdapter extends RecyclerView.Adapter<SettingProfileAdapter.ViewHolder> {

    private List<ProfileAccount> accounts;
    private final Context context;
    private ProfileAccount currentProfileAccount;
    private DataBaseHelper dataBaseHelper;

    public SettingProfileAdapter(List<ProfileAccount> accounts, @NonNull Context context) {
        this.accounts = accounts;
        this.context = context;
    }
    public void updateProfileAccounts(List<ProfileAccount>profileAccounts){
        accounts = profileAccounts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProfileAccount account = accounts.get(position);

        // Load the profile image using Glide library
//        Glide.with(context)
//                .load(account.getProfileImage())
//                .placeholder(R.drawable.untitled_design_5)
//                .error(R.drawable.untitled_design_7)
//                .into(holder.profileImage);

        // Set the account type text
        Glide.with(context)
                .load(account.getFilePath())
                .into(holder.profileImage);
        holder.accountType.setText(account.getUserName());
    }

    @Override
    public int getItemCount() {
        if (accounts == null) {
            return 0;
        }
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profileImage;
        public TextView accountType;
        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            accountType = itemView.findViewById(R.id.accountType);
            cardView = itemView.findViewById(R.id.login_image_card);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getBindingAdapterPosition();
                    ProfileAccount selectedProfile = accounts.get(adapterPosition);

                    // set selected profile as current profile
                    Intent intent = new Intent(context, ViewProfileActivity.class);
                    intent.putExtra("profile",selectedProfile);
                    context.startActivity(intent);
                }
            });
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Handle long press here
                    int adapterPosition = getBindingAdapterPosition();
                    ProfileAccount selectedProfile = accounts.get(adapterPosition);

                    // Show a confirmation dialog or perform any other action to delete the account
                    showDeleteConfirmationDialog(selectedProfile);

                    // Return true to consume the long press event
                    return true;
                }
            });
        }

            private void showDeleteConfirmationDialog(final ProfileAccount selectedProfile) {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete this account?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform the deletion here
                                deleteAccount(selectedProfile);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            private void deleteAccount(ProfileAccount profile) {
                // Perform the deletion logic here
                // ...
                DataBaseHelper dataBaseHelper = new DataBaseHelper(context.getApplicationContext());
                boolean accountSavedLocally = dataBaseHelper.deleteProfileAccount(profile.getId());

                updateProfileAccounts(dataBaseHelper.getAllProfileAccounts());

                Toast.makeText(context, "Profile deleted  " + accountSavedLocally, Toast.LENGTH_SHORT).show();

            }

        }


    private void setCurrentProfile(ProfileAccount selectedProfile) {
        SharedPreferences sharedPreferences =  context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentProfileAccountRef", selectedProfile.getReferenceNumber());
        editor.putString("currentProfileAccountType",selectedProfile.getAccountType());
        editor.putString("currentAccountFullName",selectedProfile.getUserName());
        editor.apply();
    }
}
