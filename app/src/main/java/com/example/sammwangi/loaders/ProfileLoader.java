package com.example.sammwangi.loaders;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.R;
import com.example.sammwangi.services.ProfileApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileLoader extends AsyncTaskLoader<ProfileAccount> {
    private String baseUrl;
    private String email;
    private String TAG = ProfileLoader.class.getSimpleName();

    public ProfileLoader(@NonNull Context context, String email) {
        super(context);
        this.baseUrl = context.getResources().getString(R.string.base_url_title);
        this.email = email;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public ProfileAccount loadInBackground() {

        try {
            String apiUrl = baseUrl + "/api/profiles/";

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(apiUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            ProfileApiService apiService = retrofit.create(ProfileApiService.class);
            Call<ProfileAccount> call = apiService.getProfileByEmail(email);
            Response<ProfileAccount> response = call.execute();

            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e(TAG, "Error: " + response.code() + " - " + response.message());
            }
        } catch (
                IOException e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
        return null;
    }

    private void showToast(final String message) {
        // Show a toast message on the UI thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

