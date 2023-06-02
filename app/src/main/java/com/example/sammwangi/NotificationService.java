package com.example.sammwangi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NotificationService {
    @POST("notification/send") // Replace with your actual endpoint URL
    Call<Void> sendNotification(@Body NotificationRequest notificationRequest);
}