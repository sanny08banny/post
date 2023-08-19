package com.example.sammwangi.services;

import com.example.sammwangi.DAOs.ActivityItem;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ActivityApiService {
    @Multipart
    @POST("activity-items/new")
    Call<ResponseBody> saveActivity(@Part("item") ActivityItem activityItem,
                                    @Part MultipartBody.Part filePart,
                                    @Query("email") String email);
    @GET("activity-items/all")
    Call<List<ActivityItem>> getAllActivities();
}
