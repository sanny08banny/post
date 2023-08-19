package com.example.sammwangi.services;

import com.example.sammwangi.DAOs.Page;
import com.example.sammwangi.DAOs.PostItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostsService {
    @POST("/api/posts/{referenceNumber}")
    Call<PostItem> savePost(@Path("referenceNumber") String referenceNumber,
                            @Body PostItem postItem);
    @GET("allPosts")
    Call<List<PostItem>> getAllPosts();


    @GET("/BySubCounty/{subCounty}")
    Call<Page<PostItem>> getPostItemsBySubCounty(@Path("subCounty") String subCounty);
    @GET("/api/posts/random/{subcounty}")
    Call<Page<PostItem>> getRandomPostsBySubcounty(
            @Path("subcounty") String subcounty,
            @Query("pageSize") int pageSize
    );
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
    Call<List<PostItem>> getPostsByQueryAndDateRange(@Query("start-date") String startDate,
            @Query("end-date") String endDate,
            @Query("query") String query);

    @GET("by-profile-email-and-dateRange")
    Call<List<PostItem>> getPostsByEmailAndDateRange(
            @Query("email") String email,
            @Query("start-date") String startDate,
            @Query("end-date") String endDate
    );

    @GET("paginated_posts")
    Call<Page<PostItem>> getAllPostsPaged(@Query("page") int page,
                                          @Query("pageSize") int pageSize);
    @GET("allPostsByRef/{referenceNumber}")
    Call<List<PostItem>> getMyPosts(@Path("referenceNumber") String query);
}
