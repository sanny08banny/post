package com.example.sammwangi.loaders;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.R;
import com.example.sammwangi.services.PostsService;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SavePostWithSettingsLoader extends AsyncTaskLoader<PostItem> {
    private final String referenceNumber;
    private final PostItem postItem;
    private String baseUrl;

    public SavePostWithSettingsLoader(Context context, String referenceNumber, PostItem postItem) {
        super(context);
        this.referenceNumber = referenceNumber;
        this.postItem = postItem;
        this.baseUrl = context.getResources().getString(R.string.base_url_title);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public PostItem loadInBackground() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl) // Replace with your server base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PostsService apiService = retrofit.create(PostsService.class);
            Call<PostItem> call = apiService.savePost(referenceNumber, postItem);

            Response<PostItem> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                // Handle error if the API call is not successful
                // For example, you can throw an exception or return null
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

