package com.example.sammwangi.loaders;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import com.example.sammwangi.DAOs.ActivityItem;
import com.example.sammwangi.R;
import com.example.sammwangi.services.ActivityApiService;

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

public class ActivityRetrieverLoader extends AsyncTaskLoader<List<ActivityItem>> {
    private static final String TAG = ActivityRetrieverLoader.class.getSimpleName();
    private List<ActivityItem> activityItems;
    private String baseUrl;

    public ActivityRetrieverLoader(Context context) {
        super(context);
        this.baseUrl = context.getString(R.string.base_url_title);
    }

    @Override
    protected void onStartLoading() {
        if (activityItems != null) {
            deliverResult(activityItems);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<ActivityItem> loadInBackground() {
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

            String url = baseUrl + "/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ActivityApiService service = retrofit.create(ActivityApiService.class);

            Call<List<ActivityItem>> call = service.getAllActivities();
            Response<List<ActivityItem>> response = call.execute();

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
    public void deliverResult(List<ActivityItem> data) {
        activityItems = data;
        super.deliverResult(data);
    }
    public String getCurrentEmail(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs",MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileAccountEmail",null);
    }
    public String getCurrentPassWord(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs",MODE_PRIVATE);
        return sharedPreferences.getString("currentPassword",null);
    }
}
