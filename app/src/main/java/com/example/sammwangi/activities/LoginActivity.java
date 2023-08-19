package com.example.sammwangi.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.database_helpers.DataBaseHelper;
import com.example.sammwangi.R;
import com.example.sammwangi.adapters.ProfileAccountAdapter;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CREATE_PROFILE_REQUEST = 54;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private CardView cardView;
    private ImageButton editAccountsButtons;
    private DataBaseHelper dataBaseHelper;
    private List<ProfileAccount> accountTypeArrayList;
    private Context context;
    private ProfileAccountAdapter profileAccountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editAccountsButtons = findViewById(R.id.edit_account_button);
        editAccountsButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton editAccountsButton = findViewById(R.id.edit_account_button);
                editAccountsButton.setVisibility(View.VISIBLE);
                editAccountsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                        v.getContext().startActivity(intent);
                    }
                });
            }
        });

        recyclerView = findViewById(R.id.login_profiles_recycler);

        accountTypeArrayList = new ArrayList<>();

        dataBaseHelper = new DataBaseHelper(LoginActivity.this);
        accountTypeArrayList = dataBaseHelper.getAllProfileAccounts();


        profileAccountAdapter = new ProfileAccountAdapter(accountTypeArrayList, this);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);

        recyclerView.setAdapter(profileAccountAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        profileAccountAdapter.notifyDataSetChanged();

        cardView = findViewById(R.id.add_login_image_card);
        cardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), CreateProfileActivity.class);
        intent.putExtra("primaryCall",false);
        startActivityForResult(intent,CREATE_PROFILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_PROFILE_REQUEST && resultCode == RESULT_OK && data != null){
            boolean accountCreatedSuccessfully = data.getBooleanExtra("account_created", false);
            // Handle the result here based on the accountCreatedSuccessfully value
            if (accountCreatedSuccessfully){
                accountTypeArrayList.clear();
                accountTypeArrayList.addAll(dataBaseHelper.getAllProfileAccounts());
                profileAccountAdapter.notifyDataSetChanged();
            }
        }
    }
}