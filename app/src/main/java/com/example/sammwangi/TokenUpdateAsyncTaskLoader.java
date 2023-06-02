package com.example.sammwangi;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TokenUpdateAsyncTaskLoader extends AsyncTaskLoader<Response> {
    private final String email;
    private final String firebaseToken;
    private String baseUrl;

    public TokenUpdateAsyncTaskLoader(@NonNull Context context, String email, String firebaseToken) {
        super(context);
        this.email = email;
        this.firebaseToken = firebaseToken;
    }

    @Nullable
    @Override
    public Response loadInBackground() {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // Create the request body with the email and firebaseToken
        RequestBody body = RequestBody.create(mediaType, firebaseToken);

        // Create the request with the POST method and the email as the path variable
        baseUrl = getContext().getString(R.string.base_url_title);
        Request request = new Request.Builder()
                .url(baseUrl + "/api/profiles/tokenUpdate/" + email)
                .post(body)
                .build();

        try {
            // Execute the request
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
