package com.example.sammwangi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.R;
import com.google.android.material.appbar.MaterialToolbar;

public class ManageProfiles extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView publicViewDescriptionText,dateJoinedText;
    private EditText userNameEditText;
    private ImageButton changeUserName,closeWindow;
    private ImageView profileImage;
    private ProfileAccount profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profiles);
        toolbar = findViewById(R.id.manage_profile_toolbar);
        dateJoinedText = findViewById(R.id.date_joined);
        userNameEditText = findViewById(R.id.username_edit_text);
        profileImage = findViewById(R.id.profile_image);
        changeUserName = findViewById(R.id.change_user_name_button);
        closeWindow = findViewById(R.id.close_window);
        setSupportActionBar(toolbar);


        profile = getIntent().getParcelableExtra("profile");
        if (profile != null){
            glideImage(profile.getFilePath(),profileImage);
            userNameEditText.setHint(profile.getUserName());

            if (profile.getTimestamp() != null){
                dateJoinedText.setText(profile.getTimestamp());
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("About");
        }

        closeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        changeUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserNameLoader();
            }
        });

        publicViewDescriptionText = findViewById(R.id.public_view_description_text);
        String descriptionText = getString(R.string.public_view_description);

        // Find the index of the clickable text "[CHANGE PREFERENCES]"
        int changePreferencesStartIndex = descriptionText.indexOf("[CHANGE PREFERENCES]");

        // Only proceed if the clickable text is found in the original string
        if (changePreferencesStartIndex != -1) {
            SpannableString spannableString = new SpannableString(descriptionText);

            // Create a ClickableSpan for the clickable text
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    // Perform your action here, e.g., open the recommended activity
                    Intent intent = new Intent(ManageProfiles.this, SettingsActivity.class);
                    startActivity(intent);
                }
            };

            // Set the ClickableSpan to the part of the text that needs to be clickable
            spannableString.setSpan(clickableSpan, changePreferencesStartIndex, changePreferencesStartIndex + "[CHANGE PREFERENCES]".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Set the modified SpannableString to the TextView
            publicViewDescriptionText.setText(spannableString);
            publicViewDescriptionText.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            // If the clickable text is not found, simply set the original text to the TextView
            publicViewDescriptionText.setText(descriptionText);
        }
    }

    private void changeUserNameLoader() {
    }

    private void glideImage(String filePath, ImageView profileImage) {
        Glide.with(this)
                .load(filePath)
                .into(profileImage);
    }
}