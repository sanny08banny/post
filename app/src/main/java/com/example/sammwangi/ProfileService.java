package com.example.sammwangi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProfileService {
    @POST("/new")
    Call<ProfileAccount> saveNewProfile(@Body ProfileAccount profileAccount);

    @GET("ByEmail/{email}")
    Call<ProfileAccount> getProfileByEmail(@Path("email")String email);
}
