package com.example.sammwangi.loaders;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.R;
import com.example.sammwangi.services.ProfileApiService;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminUserLoader extends AsyncTaskLoader<Response<String>> {
    private final ProfileAccount adminUser;
    private String baseUrl;

    public AdminUserLoader(Context context) {
        super(context);
        this.adminUser = new ProfileAccount();
        this.baseUrl = context.getResources().getString(R.string.base_url_title);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Response<String> loadInBackground() {
        // Replace BASE_URL with the base URL of your API endpoint
        String BASE_URL = baseUrl + "/api/profiles/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Replace ApiService with the interface that defines the API endpoint and method
        ProfileApiService apiService = retrofit.create(ProfileApiService.class);
        adminUser.setAccountType("Admin");

        Call<Response<String>> call = apiService.saveNewAdminProfileAccess(adminUser);
        try {
            return call.execute().body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

