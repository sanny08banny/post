package com.example.sammwangi.loaders;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.sammwangi.DAOs.ActivityItem;
import com.example.sammwangi.R;
import com.example.sammwangi.services.ActivityApiService;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateActivityLoader extends AsyncTaskLoader<ResponseBody> {
    private static final String TAG = CreateActivityLoader.class.getSimpleName();
    private ActivityItem activityItem;
    private File file;
    private String baseUrl,email;

    public CreateActivityLoader(@NonNull Context context, ActivityItem activityItem, File file, String email) {
        super(context);
        this.activityItem = activityItem;
        this.file = file;
        this.baseUrl = context.getString(R.string.base_url_title);
        this.email = email;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public ResponseBody loadInBackground() {
        try {
        String url = baseUrl + "/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ActivityApiService service = retrofit.create(ActivityApiService.class);
        // Create multipart request body for the file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                Response<ResponseBody> response = service.saveActivity(activityItem, filePart,email).execute();
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Activity saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                    // Handle the error response
                }
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                // Handle the failure
            }
        return null;
    }
}

