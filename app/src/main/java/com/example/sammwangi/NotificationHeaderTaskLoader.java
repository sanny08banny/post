package com.example.sammwangi;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationHeaderTaskLoader extends AsyncTaskLoader<Boolean> {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final String accountType;
    private String baseUrl;
    private final NotificationRequest notificationMessage;

    public NotificationHeaderTaskLoader(Context context, String accountType, NotificationRequest notificationMessage) {
        super(context);
        this.accountType = accountType;
        this.notificationMessage = notificationMessage;
    }

    @Nullable
    @Override
    public Boolean loadInBackground() {
        OkHttpClient client = new OkHttpClient();
        baseUrl = getContext().getString(R.string.base_url_title);

        String url = baseUrl + "/notification/send/headers/" + accountType;

        // Prepare the request body
        String requestBody = "{\"title\":\"" + notificationMessage.getTitle() + "\",\"body\":\"" +
                notificationMessage.getBody() + "\",\"notificationType\":\"" +
                notificationMessage.getNotificationType() + "\"}";

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestBody, JSON))
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
