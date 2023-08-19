package com.example.sammwangi.loaders;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.example.sammwangi.DAOs.Page;
import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.services.PostsService;
import com.example.sammwangi.R;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PostItemPagedLoader extends AsyncTaskLoader<List<PostItem>> {
    private final int page;
    private final int pageSize;
    private List<PostItem> cachedData;
    private String baseUrl;
    private String subCounty;

    public PostItemPagedLoader(Context context, int page, int pageSize, String subCounty) {
        super(context);
        this.page = page;
        this.pageSize = pageSize;
        this.baseUrl = context.getResources().getString(R.string.base_url_title);
        this.subCounty = subCounty;
    }

    @Override
    protected void onStartLoading() {
        if (cachedData != null) {
            // If we have cached data, deliver it immediately.
            deliverResult(cachedData);
        } else {
            forceLoad(); // No cached data, start loading from the API.
        }
    }

    @Override
    public List<PostItem> loadInBackground() {
        try {
            String url = baseUrl + "/api/posts/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PostsService service = retrofit.create(PostsService.class);

            Call<Page<PostItem>> call;
            // Make the API call using Retrofit2 based on the 'endpoint' parameter.
            if (subCounty != null){
                call = service.getRandomPostsBySubcounty(subCounty,pageSize);
            }else {
                call = service.getAllPostsPaged(page, pageSize);
            }

            Response<Page<PostItem>> response = call.execute();
            if (response.isSuccessful()) {
                Page<PostItem> pageResponse = response.body();
                if (pageResponse != null) {
                    return pageResponse.getContent();
                }
            }
            // Handle error cases here.
            // For example, you can log the error or return an empty list.
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle network-related errors here.
            return null;
        }
    }

    @Override
    public void deliverResult(List<PostItem> data) {
        cachedData = data;
        super.deliverResult(data);
    }
}


