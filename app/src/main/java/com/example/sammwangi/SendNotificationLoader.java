package com.example.sammwangi;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotificationLoader extends AsyncTaskLoader<Void> {
    private final NotificationRequest notificationRequest;

    public SendNotificationLoader(Context context, NotificationRequest notificationRequest) {
        super(context);
        this.notificationRequest = notificationRequest;
    }

    @Override
    public Void loadInBackground() {
        // Make the HTTP POST request to send the notification
        // using the provided Spring app endpoint

        // Implement the HTTP request code here
        // (e.g., using HttpURLConnection or OkHttp)
        try {
            // Prepare the request body
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            String baseUrl = getContext().getString(R.string.base_url_title);
            String json = "{\"title\":\"" + notificationRequest.getTitle() +
                    "\",\"body\":\"" + notificationRequest.getBody() +
                    "\",\"notificationType\":\"" + notificationRequest.getNotificationType() +"\"}";
            RequestBody requestBody = RequestBody.create(json, mediaType);

            // Create the request
            Request request = new Request.Builder()
                    .url(baseUrl + "/notification/send")
                    .post(requestBody)
                    .build();

            // Send the request
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();

            // Process the response if needed

            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
