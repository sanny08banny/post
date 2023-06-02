package com.example.sammwangi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private CardView cardView;
    private ImageButton editAccountsButtons;
    private List<ProfileAccount> accountTypeArrayList;
    private Context context;

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

        DataBaseHelper dataBaseHelper = new DataBaseHelper(LoginActivity.this);
        accountTypeArrayList = dataBaseHelper.getAllAccounts();


        ProfileAccountAdapter profileAccountAdapter = new ProfileAccountAdapter(accountTypeArrayList, this);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

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
        v.getContext().startActivity(intent);
    }
}