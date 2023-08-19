package com.example.sammwangi.loaders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.R;
import com.example.sammwangi.activities.LoginActivity;
import com.example.sammwangi.database_helpers.DataBaseHelper;
import com.example.sammwangi.services.ProfileApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFetchRunnable implements Runnable {
    private final String email;
    private final String password;
    private final Context context;
    private DataBaseHelper dataBaseHelper;

    public ProfileFetchRunnable(String email, String password, Context context) {
        this.email = email;
        this.password = password;
        this.context = context;
    }

    @Override
    public void run() {
        // Make API call to retrieve the profile account using the provided email
        // Replace the URL with your actual Spring application endpoint
        String baseUrl = context.getString(R.string.base_url_title);
        String apiUrl = baseUrl + "/api/profiles/";
        Log.e("ProfileFetchRunnable", "Email chosen: " + apiUrl);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ProfileApiService apiService = retrofit.create(ProfileApiService.class);
        Call<ProfileAccount> call = apiService.getProfileByEmail(email);

        call.enqueue(new Callback<ProfileAccount>() {

            @Override
            public void onResponse(Call<ProfileAccount> call, Response<ProfileAccount> response) {
                ProfileAccount profileAccount = response.body();
                if (profileAccount != null && profileAccount.getPassword().equals(password)) {
                    // Save the profile account to the SQLite database
                    saveProfileToDatabase(profileAccount);
                    Log.e("ProfileFetchRunnable", "Email chosen: " + profileAccount.getEmail());


                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ProfileAccount> call, Throwable t) {
                showToast("Failed to fetch profile");
            }
        });
    }

    private void saveProfileToDatabase(ProfileAccount profileJson) {
        // Parse the JSON or extract necessary data and save it to the SQLite database
        // Use your preferred database library (e.g., Room, SQLiteOpenHelper, etc.)
        dataBaseHelper = new DataBaseHelper(context);
        boolean accountSavedLocally = dataBaseHelper.addProfileAccount(profileJson);
        Toast.makeText(context, "Profile saved here " + accountSavedLocally, Toast.LENGTH_SHORT).show();

    }

    private void showToast(final String message) {
        // Show a toast message on the UI thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}