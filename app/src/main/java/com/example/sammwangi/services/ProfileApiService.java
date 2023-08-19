package com.example.sammwangi.services;

import com.example.sammwangi.DAOs.ProfileAccount;
import com.example.sammwangi.DAOs.ProfileDTO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ProfileApiService {
    @POST("/new")
    Call<ProfileAccount> saveNewProfile(@Body ProfileAccount profileAccount);
    @POST("admin")
    Call<Response<String>> saveNewAdminProfileAccess(@Body ProfileAccount profileAccount);


    @GET("ByEmail/{email}")
    Call<ProfileAccount> getProfileByEmail(@Path("email")String email);

    @Multipart
    @POST("api/profiles/new")
    Call<ResponseBody> saveProfile(@Part("item") ProfileAccount profile, @Part MultipartBody.Part file);
    @GET("api/profiles/adminAndSubCountyHead")
    Call<List<ProfileDTO>> getAdmins();
}
