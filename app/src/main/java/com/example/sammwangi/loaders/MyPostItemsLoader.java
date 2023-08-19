package com.example.sammwangi.loaders;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.R;
import com.example.sammwangi.services.PostsService;

import java.io.IOException;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyPostItemsLoader extends AsyncTaskLoader<List<PostItem>> {
    private static final String TAG = "StorisRetriever";
    private List<PostItem> postItems;
    private String baseUrl;
    private String query;

    public MyPostItemsLoader(@NonNull Context context, String query) {
        super(context);
        this.baseUrl = context.getString(R.string.base_url_title);
        this.query = query;
    }

    @Override
    protected void onStartLoading() {
        if (postItems != null) {
            deliverResult(postItems);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<PostItem> loadInBackground() {
        try {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            final String username = "Sanny 08 Banny";
            final String password = "200Pilot";

            // Create an Interceptor to add the authentication header
            Interceptor interceptor = chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", Credentials.basic(username, password))
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            };

            httpClient.addInterceptor(interceptor);

            String url = baseUrl + "/api/posts/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PostsService service = retrofit.create(PostsService.class);

            Call<List<PostItem>> call;
            call = service.getMyPosts(query);
            Response<List<PostItem>> response = call.execute();

            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e(TAG, "Error: " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deliverResult(List<PostItem> data) {
        postItems = data;
        super.deliverResult(data);
    }
}

