package com.example.sammwangi;

import java.util.List;

import kotlin.ParameterName;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostsService {
    @GET("allPosts")
    Call<List<PostItem>> getAllPosts();

    @GET("/BySubCount/{subCounty}")
    Call<List<PostItem>> getPostItemsBySubCounty(@Path("subCounty") String subCounty);

    @GET("total-balance")
    Call<Integer> getTotalBalance();

    @GET("by-date-range")
    Call<List<PostItem>> getPostsByDateRange(@Query("start-date") String startDate,
                                             @Query("end-date") String endDate);
    @GET("/ByFullName/{fullName}")
    Call<List<PostItem>> getPostItemsByFullName(@Path("fullName") String fullName);

    @GET("ByEmail")
    Call<List<PostItem>> getPostItemsByEmail(@Query("email") String email);
    @GET("by-profile-fullName-and-dateRange")
    Call<List<PostItem>> getPostsByNameAndDateRange(
            @Query("fullName") String fullName,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate);
    @GET("by-profile-email-and-dateRange")
    Call<List<PostItem>> getPostsByEmailAndDateRange(
            @Query("email") String email,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

}
