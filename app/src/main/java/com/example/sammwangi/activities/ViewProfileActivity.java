package com.example.sammwangi.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.R;
import com.example.sammwangi.database_helpers.DataBaseHelper;
import com.example.sammwangi.fragments.MyPostsFragment;
import com.example.sammwangi.loaders.ProfileLoader;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ViewProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ProfileAccount> {
    private static final int GET_PROFILE = 87;
    private MaterialToolbar toolbar;
    private ImageView imageView;
    private TextView userName;
    private ProfileAccount selectedAccount;
    private MaterialButton editProfile;
    private FrameLayout frameLayout;
    private ProfileAccount profile;
    private String selectedAccountString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        toolbar = findViewById(R.id.view_profile_toolbar);
        imageView = findViewById(R.id.view_profile_image);
        userName = findViewById(R.id.view_profile_username);
        frameLayout = findViewById(R.id.my_posts_frame_layout);
        editProfile = findViewById(R.id.edit_profile_button);
        setSupportActionBar(toolbar);


        profile = getIntent().getParcelableExtra("profile");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(profile.getUserName());
        }

        if (profile.getEmail().matches(getCurrentProfileEmail())) {
            Glide.with(this)
                    .load(profile.getFilePath())
                    .into(imageView);
            userName.setText(profile.getUserName());

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.my_posts_frame_layout, new MyPostsFragment(profile.getReferenceNumber()))
                    .commit();
        }else {
            fetchProfile();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditAccount();
            }
        });

    }

    private void fetchProfile() {
        LoaderManager.getInstance(this).initLoader(GET_PROFILE,null,this);
    }

    private void openEditAccount() {
        if (profile != null){
            Intent intent = new Intent(ViewProfileActivity.this, ManageProfiles.class);
            intent.putExtra("profile", profile);
            startActivity(intent);
        }else {
            Intent intent = new Intent(ViewProfileActivity.this, ManageProfiles.class);
            intent.putExtra("profile", selectedAccount);
            startActivity(intent);
        }
    }
    public String getCurrentProfileEmail() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("currentEmail", null);
    }

    @NonNull
    @Override
    public Loader<ProfileAccount> onCreateLoader(int id, @Nullable Bundle args) {
        return new ProfileLoader(this,profile.getEmail());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ProfileAccount> loader, ProfileAccount data) {
        if (data != null){
            selectedAccount = data;
            Glide.with(this)
                    .load(selectedAccount.getFilePath())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_baseline_account_box_32) // Placeholder image while loading
                            .error(R.drawable.outline_error_outline_48)      // Error image if loading fails
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
            userName.setText(selectedAccount.getUserName());

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.my_posts_frame_layout, new MyPostsFragment(selectedAccount.getReferenceNumber()))
                    .commit();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ProfileAccount> loader) {

    }
}