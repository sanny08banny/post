package com.example.sammwangi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

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
        Log.e("ProfileFetchRunnable","Email chosen: " + apiUrl);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ProfileService apiService = retrofit.create(ProfileService.class);
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
        // Perform the API request using your preferred networking library (e.g., Retrofit, OkHttp, etc.)

        // Example using OkHttp
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(apiUrl)
//                .build();
//
//        try {
//            Response response = client.newCall(request).execute();
//            if (response.isSuccessful()) {
//                // Get the profile account details from the response
//                assert response.body() != null;
//                String profileJson = response.body().string();
//
//                // Parse the JSON response to a Profile object or extract the necessary data
//                ProfileAccount profileAccount = parseProfileJson(profileJson);
//
//                // Verify the password
//                if (profileAccount != null && profileAccount.getPassword().equals(password)) {
//                    // Save the profile account to the SQLite database
//                    saveProfileToDatabase(profileAccount);
//                    Log.e("ProfileFetchRunnable","Email chosen: " + profileAccount.getEmail());
//
//
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent intent = new Intent(context, LoginActivity.class);
//                            context.startActivity(intent);
//                        }
//                    });
//                    // Start the next activity using an Intent
//                } else {
//                    // Show an error message that the email or password is incorrect
//                    showToast("Invalid email or password");
//                }
//            } else {
//                // Show an error message that the email is not available
//                showToast("Email is not available");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Show an error message for the API call failure
//            showToast("Failed to fetch profile");
//        }
    }

    private ProfileAccount parseProfileJson(String profileJson) {
        // Parse the JSON response to a Profile object or extract the necessary data
        // Return the Profile object if parsing is successful, otherwise return null
        // Handle the parsing according to your JSON structure and library (e.g., Gson, JSONObject, etc.)
        try {
            JSONObject jsonObject = new JSONObject(profileJson);
            int id = jsonObject.getInt("id");
            String accountType = jsonObject.getString("accountType");
            String fullName = jsonObject.getString("fullName");
            String email = jsonObject.getString("email");
            String referenceNumber = jsonObject.getString("referenceNumber");
            String subCounty = jsonObject.getString("subCounty");
            String password = jsonObject.getString("password");
            String repeatPassword = jsonObject.getString("repeatPassword");
            String userName = jsonObject.getString("userName");
            String tokenId = jsonObject.getString("tokenId");

            // Create a new Profile object
            return new ProfileAccount(id, accountType, fullName, email, referenceNumber, password, subCounty, repeatPassword, userName, tokenId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void saveProfileToDatabase(ProfileAccount profileJson) {
        // Parse the JSON or extract necessary data and save it to the SQLite database
        // Use your preferred database library (e.g., Room, SQLiteOpenHelper, etc.)
        dataBaseHelper = new DataBaseHelper(context);
        boolean accountSavedLocally = dataBaseHelper.addProfile(profileJson);
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