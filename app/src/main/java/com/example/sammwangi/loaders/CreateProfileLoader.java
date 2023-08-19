package com.example.sammwangi.loaders;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.sammwangi.database_helpers.DataBaseHelper;
import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.services.ProfileApiService;
import com.example.sammwangi.R;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateProfileLoader extends AsyncTaskLoader<ResponseBody> {
    private static final String TAG = CreateProfileLoader.class.getSimpleName();
    private ProfileAccount profile;
    private File file;
    private String baseUrl;
    private DataBaseHelper profileDatabaseHelper;

    public CreateProfileLoader(@NonNull Context context, ProfileAccount profile, File file) {
        super(context);
        this.profile = profile;
        this.file = file;
        this.baseUrl = context.getString(R.string.base_url_title);
        this.profileDatabaseHelper = new DataBaseHelper(getContext());
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

        ProfileApiService service = retrofit.create(ProfileApiService.class);
        // Create multipart request body for the file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                Response<ResponseBody> response = service.saveProfile(profile, filePart).execute();
                if (response.isSuccessful()) {
                    // Handle the successful response
                    ProfileAccount savedProfile = profile;
                    savedProfile.setFilePath(String.valueOf(file));
                    boolean isSavedLocally = profileDatabaseHelper.addProfileAccount(savedProfile);

                    if (isSavedLocally) {
                        return response.body();
                    }else {
                        Toast.makeText(getContext(), "Error saving profile Locally", Toast.LENGTH_SHORT).show();
                        return null;
                    }
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

